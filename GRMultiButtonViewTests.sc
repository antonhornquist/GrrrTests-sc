GRMultiButtonViewTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// initialization
	test_the_button_array_size_of_a_multibuttonview_should_by_default_be_of_the_same_size_as_the_view {
		var view = GRMultiButtonView.newDetached(4, 4);

		this.assertEqual(
			view.buttonArraySize,
			Point.new(4, 4)
		);
	}

	// button array size
	test_it_should_be_possible_to_change_the_button_array_size_of_a_multibuttonview {
		var view = GRMultiButtonView.newDetached(4, 4);
		view.buttonArraySize = Point.new(2, 2);

		this.assertEqual(
			view.buttonArraySize,
			Point.new(2, 2)
		);
	}

	test_it_should_not_be_possible_to_change_the_button_array_size_of_a_multibuttonview_so_that_num_cols_of_the_view_is_not_divisable_by_num_button_cols {
		var view = GRMultiButtonView.newDetached(4, 4);

		this.assertErrorThrown(Error) { view.buttonArraySize = Point.new(3, 2) };
	}

	test_it_should_not_be_possible_to_change_the_button_array_size_of_a_multibuttonview_so_that_num_rows_of_the_view_is_not_divisable_by_num_button_rows {
		var view = GRMultiButtonView.newDetached(4, 4);

		this.assertErrorThrown(Error) { view.buttonArraySize = Point.new(2, 3) };
	}

	// value
	test_the_value_of_a_multibuttonview_should_be_a_map_of_the_value_of_its_buttons {
		var view = GRMultiButtonView.newDetached(4, 4);

		this.assertEqual(
			view.value,
			[
				[false, false, false, false],
				[false, false, false, false],
				[false, false, false, false],
				[false, false, false, false]
			]
		);
	}

	test_when_a_multibuttonviews_value_is_updated_by_a_call_to_value_action_a_main_action_notification_should_be_sent {
		var view = GRMultiButtonView.newDetached(2, 2);
		var listener = MockActionListener.new(view);

		view.valueAction = [
			[false, true],
			[false, true]
		];

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[
						view,
						[
							[false, true],
							[false, true],
						]
					]
				]
			)
		);
	}

	test_when_a_multibuttonviews_value_is_updated_by_a_call_to_value_action_button_value_changed_notifications_should_be_sent_for_all_buttons_whose_value_has_changed {
		var view = GRMultiButtonView.newDetached(2, 2);
		var listener = MockButtonValueChangedListener.new(view);

		view.valueAction = [
			[false, true],
			[false, true]
		];

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					(
						multiButtonView: view,
						x: 0,
						y: 1,
						value: true
					),
					(
						multiButtonView: view,
						x: 1,
						y: 1,
						value: true
					)
				]
			)
		);
	}

	// button events
	test_when_a_multibuttonviews_value_is_updated_by_a_button_event_a_main_action_notification_should_be_sent {
		var view = GRMultiButtonView.newDetached(2, 2);
		var listener = MockActionListener.new(view);

		view.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[
						view,
						[
							[true, false],
							[false, false],
						]
					]
				]
			)
		);
	}

	test_when_a_multibuttonviews_value_is_updated_by_a_button_event_a_button_value_changed_notification_should_be_sent {
		var view = GRMultiButtonView.newDetached(2, 2);
		var listener = MockButtonValueChangedListener.new(view);

		view.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					(
						multiButtonView: view,
						x: 0,
						y: 0,
						value: true
					)
				]
			)
		);
	}

	// string representation
	test_the_plot_of_a_multibuttonview_should_not_indicate_its_internal_child_views {
		var view = GRMultiButtonView.newDetached(4, 4);

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 - - - -\n" ++
			"1 - - - -    1 - - - -\n" ++
			"2 - - - -    2 - - - -\n" ++
			"3 - - - -    3 - - - -\n",
			view.asPlot
		);
	}
}
