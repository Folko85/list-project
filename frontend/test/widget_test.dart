import 'package:flutter_test/flutter_test.dart';

import 'package:frontend/main.dart';

void main() {
  testWidgets('App renders empty page', (WidgetTester tester) async {
    await tester.pumpWidget(const FrontendApp());

    expect(find.byType(Scaffold), findsOneWidget);
  });
}
