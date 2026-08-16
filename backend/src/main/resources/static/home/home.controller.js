(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', 'TaskService', 'AuthenticationService', 'FlashService'];

    function HomeController(UserService, TaskService, AuthenticationService, FlashService) {
        var vm = this;

        vm.user = null;
        vm.allUsers = [];

        vm.logout = logout;

        initController();

        function initController() {
            initApplication();
            loadCurrentUser();
        }

        function loadCurrentUser() {
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            UserService.GetByUsername(currentUser.username)
                .then(user => {
                    vm.user = user;
                });
        }

        function loadLastTasks() {
            const promise = TaskService.getTasks();
            promise.then(tasks => onTasksReceived(tasks)).catch(error => FlashService.Error(error));
        }

        function logout() {
            const promise = AuthenticationService.logout();
            promise.then(response => {
                FlashService.Success(response.data.message, true);
                localStorage.removeItem('currentUser');
                document.location.href = "/";
            }).catch(error => FlashService.Error(error));
        }

        function initApplication() {
            $('.messages-and-users').css({display: 'flex'});
            $('.controls').css({display: 'flex'});
            loadLastTasks();
            $('.new-task').keypress(function (e) {
                const key = e.which;
                if (key === 13)  // the enter key code
                {
                    $('.create-task').click();
                    return false;
                }
            });

            $('.create-task').on('click', function () {
                let task = $('.new-task').val().trim();
                const promise = TaskService.createTask(task);
                $('.new-task').val('')
                promise.then(TaskService.getTasks).then(onTasksReceived);
            });

        }

        function onTasksReceived(tasks) {
            const result = document.querySelector('#tasks-result');
            result.innerHTML = '';
            for (let i = 0; i < tasks.length; i++) {
                const tr = document.createElement('tr');
                const td1 = document.createElement('td');
                const td2 = document.createElement('td');
                const td3 = document.createElement('td');
                const deleteButton = document.createElement('button');
                const editButton = document.createElement('button');
                td1.innerHTML = (i + 1).toString();
                td1.width = 50;
                td1.align = "center"
                td2.innerHTML = tasks[i].title;
                td2.id = tasks[i].id;
                td2.width = 500;
                td2.align = "center";
                editButton.innerHTML = "Изменить";
                deleteButton.innerHTML = "Удалить";
                result.appendChild(tr)
                tr.appendChild(td1);
                tr.appendChild(td2);
                tr.appendChild(td3);
                td3.appendChild(editButton);
                td3.appendChild(deleteButton);
                deleteButton.addEventListener("click", () => {
                    const promise = TaskService.deleteTask(tasks[i].id);
                    promise.then(TaskService.getTasks).then(onTasksReceived);
                });
                editButton.addEventListener("click", () => onEditClick(td2, tasks[i]));
            }

        }

        function onEditClick(td, el) {
            td.innerHTML = '';
            const input = document.createElement('input');
            input.type = "text";
            input.value = el.title;
            td.appendChild(input);
            input.addEventListener("change", () => {
                const promise = TaskService.updateTask(input.value, el.id);
                promise.then(TaskService.getTasks).then(onTasksReceived);
            });
        }

    }

})();