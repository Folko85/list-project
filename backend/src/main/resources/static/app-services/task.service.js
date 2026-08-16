(function () {
    'use strict';

    angular
        .module('app')
        .factory('TaskService', TaskService);

    TaskService.$inject = ['$http'];

    function TaskService($http) {
        var service = {};

        service.getTasks = getTasks;
        service.createTask = createTask;
        service.updateTask = updateTask;
        service.deleteAllTasks = deleteAllTasks;
        service.deleteTask = deleteTask;

        return service;

        function getTasks() {
            const promise = axios({
                method: 'get',
                url: "/api/tasks",
                headers : $http.defaults.headers
            });
            return promise.then((response) => {
                return response.data;
            });
        }

        function createTask(title) {
            const promise = axios({
                method: 'post',
                url: "/api/tasks",
                data: {
                    title: title
                },
                headers : $http.defaults.headers
            });
            return promise.then((response) => {
                return response.data;
            });
        }

        function updateTask(title, id) {
            const promise = axios({
                method: 'put',
                url: "/api/tasks",
                data: {
                    id: id,
                    title: title
                },
                headers : $http.defaults.headers
            });
            return promise.then((response) => {
                return response.data;
            });
        }

        function deleteAllTasks() {
            const promise = axios({
                method: 'delete',
                url: "/api/tasks",
                headers : $http.defaults.headers
            });
            return promise.then((response) => {
                return response.data;
            });
        }

        function deleteTask(id) {
            const promise = axios({
                method: 'delete',
                url: "/api/tasks/" + id,
                headers : $http.defaults.headers
            });
            return promise.then((response) => {
                return response.data;
            });
        }
    }

})();
