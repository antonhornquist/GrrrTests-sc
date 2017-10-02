GRMultiToggleViewTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// initialization
	test_the_number_of_toggles_in_a_vertical_multitoggleview_should_by_default_be_the_same_as_the_number_of_columns_in_its_view {
		var view = GRMultiToggleView.newDetached(8, 4, \vertical);

		this.assertEqual(
			view.numToggles,
			8
		);
	}

	test_the_number_of_toggles_in_a_horizontal_multitoggleview_should_by_default_be_the_same_as_the_number_of_rows_in_its_view {
		var view = GRMultiToggleView.newDetached(8, 4, \horizontal);

		this.assertEqual(
			view.numToggles,
			4
		);
	}

	// number of toggles
	test_it_should_be_possible_to_change_the_number_of_toggles_in_a_multitoggleview {
		var view = GRMultiToggleView.newDetached(4, 4, \vertical);
		view.numToggles = 2;

		this.assertEqual(
			view.numToggles,
			2
		);
	}

	test_it_should_not_be_possible_to_change_the_number_of_toggles_in_a_vertical_multitoggleview_so_that_num_cols_of_the_view_is_not_divisable_by_num_toggles {
		var view = GRMultiToggleView.newDetached(8, 7, \vertical);

		this.assertErrorThrown(Error) { view.numToggles = 7 };
	}

	test_it_should_not_be_possible_to_change_the_number_of_toggles_in_a_horizontal_multitoggleview_so_that_num_rows_of_the_view_is_not_divisable_by_num_toggles {
		var view = GRMultiToggleView.newDetached(3, 4, \horizontal);

		this.assertErrorThrown(Error) { view.numToggles = 3 };
	}

	// orientation
	test_it_should_be_possible_to_change_the_orientation_of_a_multitoggleview {
 		var view = GRMultiToggleView.newDetached(7, 4, \horizontal);
 		this.assertNoErrorThrown { view.orientation = \vertical };
	}

	test_when_orientation_of_a_multitoggleview_is_changed_num_toggles_should_be_se_as_default {
 		var view = GRMultiToggleView.newDetached(7, 4, \horizontal);
 		view.orientation = \vertical;

		this.assertEqual(view.numToggles, 7);
	}

	// value
	test_the_value_of_a_multitoggleview_should_be_a_map_of_the_value_of_its_toggles {
		var view = GRMultiToggleView.newDetached(4, 4, \horizontal);

		this.assertEqual(
			view.value,
			[0, 0, 0, 0]
		);
	}

	test_when_a_multitoggleviews_value_is_updated_by_a_call_to_value_action_a_main_action_notification_should_be_sent {
		var view = GRMultiToggleView.newDetached(4, 4, \horizontal);
		var listener = MockActionListener.new(view);

		view.valueAction = [1, 0, 2, 0];

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[
						view,
						[1, 0, 2, 0]
					]
				]
			)
		);
	}

	test_when_a_multitoggleviews_value_is_updated_by_a_call_to_value_action_toggle_value_changed_notifications_should_be_sent_for_all_toggles_whose_value_has_changed {
		var view = GRMultiToggleView.newDetached(4, 4, \horizontal);
		var listener = MockToggleValueChangedListener.new(view);

		view.valueAction = [1, 0, 2, 0];

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					(
						multiToggleView: view,
						i: 0,
						value: 1
					),
					(
						multiToggleView: view,
						i: 2,
						value: 2
					)
				]
			)
		);
	}

	// button events
	test_when_a_multitoggleviews_value_is_updated_by_a_button_event_a_main_action_notification_should_be_sent {
		var view = GRMultiToggleView.newDetached(4, 4, \horizontal);
		var listener = MockActionListener.new(view);

		view.press(Point.new(3, 1));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[
						view,
						[0, 3, 0, 0]
					]
				]
			)
		);
	}

	test_when_a_multitoggleviews_value_is_updated_by_a_button_event_a_button_value_changed_notification_should_be_sent {
		var view = GRMultiToggleView.newDetached(4, 4, \horizontal);
		var listener = MockToggleValueChangedListener.new(view);

		view.press(Point.new(3, 1));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					(
						multiToggleView: view,
						i: 1,
						value: 3
					)
				]
			)
		);
	}

	// string representation
	test_the_plot_of_a_multitoggleview_should_not_indicate_its_internal_child_views {
		var view = GRMultiToggleView.newDetached(4, 4, \vertical);

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 L L L L\n" ++
			"1 - - - -    1 - - - -\n" ++
			"2 - - - -    2 - - - -\n" ++
			"3 - - - -    3 - - - -\n",
			view.asPlot
		);
	}
}
