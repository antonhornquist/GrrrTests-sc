GRToggleTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// initialization
	test_a_toggle_should_by_default_be_coupled_and_have_value_0 {
		var toggle = GRHToggle.newDetached;
		this.assert(toggle.isCoupled);
		this.assertEqual(0, toggle.value);
	}

	test_a_horizontal_toggle_should_by_default_be_4x1 {
		var toggle = GRHToggle.newDetached;
		this.assertEqual(4, toggle.numCols);
		this.assertEqual(1, toggle.numRows);
	}

	test_when_only_num_cols_is_supplied_on_creation_a_horizontal_toggle_should_get_num_rows_1 {
		var toggle = GRHToggle.newDetached(8);
		this.assertEqual(1, toggle.numRows);
	}

	test_a_vertical_toggle_should_by_default_be_1x4 {
		var toggle = GRVToggle.newDetached;
		this.assertEqual(1, toggle.numCols);
		this.assertEqual(4, toggle.numRows);
	}

	test_when_only_num_rows_is_supplied_on_creation_a_vertical_toggle_should_get_num_cols_1 {
		var toggle = GRVToggle.newDetached(nil, 8);
		this.assertEqual(1, toggle.numCols);
	}

	test_it_should_be_possible_to_create_a_decoupled_toggle_from_scratch {
		var toggle = GRHToggle.newDecoupled(nil, nil, 1, 1);
		this.assertEqual(false, toggle.isCoupled);
	}

	test_it_should_be_possible_to_create_a_nillable_toggle_from_scratch {
		var toggle = GRHToggle.newNillable(nil, nil, 1, 1);
		this.assert(toggle.isNillable);
	}

	// basic properties
	test_it_should_be_possible_to_decouple_a_toggle {
		var toggle = GRHToggle.newDetached;
		toggle.coupled = false;
		this.assertEqual(false, toggle.isCoupled);
	}

	test_it_should_be_possible_to_make_a_toggle_nillable {
		var toggle = GRHToggle.newDetached;
		toggle.nillable = true;
		this.assert(toggle.isNillable);
	}

	// toggle pressed state and toggle events
	test_a_single_view_button_press_event_should_make_a_toggle_pressed {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.press(Point.new(2, 0));
		this.assert(toggle.isPressed);
	}

	test_a_toggle_should_not_be_considered_released_until_all_view_buttons_are_released {
		var toggle = GRHToggle.newDetached(4, 1);

		toggle.press(Point.new(0, 0));

		this.assert(toggle.isPressed);

		toggle.press(Point.new(1, 0));

		this.assert(toggle.isPressed);

		toggle.release(Point.new(0, 0));

		this.assert(toggle.isPressed);

		toggle.release(Point.new(1, 0));

		this.assert(toggle.isReleased);
	}

	test_when_pressed_state_of_a_toggle_is_updated_toggle_pressed_and_released_actions_should_be_triggered {
		var toggle = GRHToggle.newDetached(4, 1);
		var pressedListener = MockTogglePressedListener.new(toggle);
		var releasedListener = MockToggleReleasedListener.new(toggle);

		toggle.press(Point.new(0, 0));
		this.assert( pressedListener.hasBeenNotifiedOf( [ [toggle] ] ) );

		toggle.press(Point.new(1, 0));
		this.assert( pressedListener.hasBeenNotifiedOf( [ [toggle] ] ) );

		toggle.release(Point.new(0, 0));
		this.assert( releasedListener.hasNotBeenNotifiedOfAnything );

		toggle.release(Point.new(1, 0));
		this.assert( releasedListener.hasBeenNotifiedOf( [ [toggle] ] ) );
	}

	test_every_view_button_press_event_on_a_toggle_should_trigger_toggle_value_pressed_action {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener = MockToggleValuePressedListener.new(toggle);

		toggle.press(Point.new(2, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[ [toggle, 2] ]
			)
		);

		toggle.press(Point.new(3, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[ [toggle, 2], [toggle, 3] ]
			)
		);

		toggle.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[ [toggle, 2], [toggle, 3], [toggle, 0] ]
			)
		);
	}

	test_if_several_buttons_get_pressed_on_view_and_the_min_and_max_values_of_the_pressed_buttons_get_changed_toggle_range_pressed_action_should_be_triggered {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener = MockToggleRangePressedListener.new(toggle);

		toggle.press(Point.new(1, 0));
		toggle.press(Point.new(3, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[ [toggle, [1, 3]] ]
			)
		);
	}

	// led events and refresh
	test_when_a_toggle_is_set_to_a_new_value_leds_should_be_refreshed_and_only_the_led_corresponding_to_the_value_should_be_lit {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener = MockViewLedRefreshedListener.new(toggle);
		toggle.id = \abc;

		toggle.value = 3;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(2, 0), on: false ),
					( source: \abc, point: Point.new(3, 0), on: true ),
				]
			)
		);
	}

	test_when_a_nillable_toggle_is_set_to_a_nil_value_leds_should_be_refreshed_and_all_leds_should_be_unlit {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener;
		toggle.id = \abc;
		toggle.nillable=true;
		listener = MockViewLedRefreshedListener.new(toggle);

		toggle.value = nil;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(2, 0), on: false ),
					( source: \abc, point: Point.new(3, 0), on: false ),
				]
			)
		);
	}

	test_if_a_non_nillable_toggle_is_set_to_a_nil_value_an_error_should_be_thrown {
		var toggle = GRHToggle.newDetached(4, 1);

		this.assertErrorThrown(Error) { toggle.value = nil };
	}

	// filled vs not filled
	test_a_toggle_that_is_not_filled_should_only_have_the_led_correspoding_to_the_current_value_lit {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.value = 2;

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 - - L -\n",
			toggle.asPlot
		);
	}

	test_a_filled_toggle_should_have_all_leds_up_to_the_current_value_lit {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.filled = true;
		toggle.value = 2;

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 L L L -\n",
			toggle.asPlot
		);
	}

	test_when_toggle_is_set_filled_all_leds_should_automatically_refresh {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener;
		toggle.id = \abc;
		toggle.value = 2;
		listener = MockViewLedRefreshedListener.new(toggle);

		toggle.filled = true;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: true ),
					( source: \abc, point: Point.new(1, 0), on: true ),
					( source: \abc, point: Point.new(2, 0), on: true ),
					( source: \abc, point: Point.new(3, 0), on: false ),
				]
			)
		);
	}

	test_when_toggle_is_set_not_filled_all_leds_should_automatically_refresh {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener;
		toggle.id = \abc;
		toggle.filled = true;
		toggle.value = 2;
		listener = MockViewLedRefreshedListener.new(toggle);

		toggle.filled = false;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(2, 0), on: true ),
					( source: \abc, point: Point.new(3, 0), on: false ),
				]
			)
		);
	}

	// thumb size
	test_a_vertical_toggle_should_by_default_have_thumb_width_set_to_the_width_of_its_view {
		var toggle = GRVToggle.newDetached(2, 4);

		this.assertEqual(2, toggle.thumbWidth);
	}

	test_a_vertical_toggle_should_by_default_have_thumb_height_1 {
		var toggle = GRVToggle.newDetached(2, 4);

		this.assertEqual(1, toggle.thumbHeight);
	}

	test_a_horizontal_toggle_should_by_default_have_thumb_width_1 {
		var toggle = GRHToggle.newDetached(4, 2);

		this.assertEqual(1, toggle.thumbWidth);
	}

	test_a_horizontal_toggle_should_by_default_have_thumb_height_set_to_the_height_of_its_view {
		var toggle = GRHToggle.newDetached(4, 2);

		this.assertEqual(2, toggle.thumbHeight);
	}

	test_it_should_be_possible_to_change_thumb_width_of_a_toggle {
		var toggle = GRHToggle.newDetached(4, 2);

		toggle.thumbWidth = 2;

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 L L - -\n" ++
			"1 - - - -    1 L L - -\n",
			toggle.asPlot
		);
	}

	test_it_should_be_possible_to_change_thumb_size_of_a_toggle {
		var toggle = GRHToggle.newDetached(4, 2);

		toggle.thumbSize = [2, 2];

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 L L - -\n" ++
			"1 - - - -    1 L L - -\n",
			toggle.asPlot
		);
	}

	test_it_should_be_possible_to_change_thumb_height_of_a_toggle {
		var toggle = GRHToggle.newDetached(4, 2);

		toggle.thumbHeight = 1;

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 L - - -\n" ++
			"1 - - - -    1 - - - -\n",
			toggle.asPlot
		);
	}

	test_it_should_not_be_possible_to_set_a_toggles_thumb_width_to_an_inconsistent_value {
		var toggle = GRHToggle.newDetached(4, 4);

		this.assertErrorThrown(Error) { toggle.thumbWidth = 3 };
	}

	test_it_should_not_be_possible_to_set_a_toggles_thumb_height_to_an_inconsistent_value {
		var toggle = GRHToggle.newDetached(4, 4);

		this.assertErrorThrown(Error) { toggle.thumbHeight = 3 };
	}

	test_when_a_toggles_thumb_size_is_changed_all_view_buttons_should_be_released {
		var toggle = GRHToggle.newDetached(4, 4);
		toggle.press(Point.new(0, 0));

		toggle.thumbSize = [2, 2];

		this.assert(toggle.allReleased);
	}

	test_when_a_toggles_thumb_size_is_changed_its_leds_should_refresh {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener = MockViewLedRefreshedListener.new(toggle);
		toggle.id = \abc;

		toggle.thumbSize = [2, 1];

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: true ),
					( source: \abc, point: Point.new(1, 0), on: true ),
					( source: \abc, point: Point.new(2, 0), on: false ),
					( source: \abc, point: Point.new(3, 0), on: false ),
				]
			)
		);
	}

	// inverted vs not inverted values
	test_a_toggle_that_does_not_have_inverted_values_should_have_correct_led_lit {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.value = 3;
		toggle.valuesAreInverted = false;

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 - - - L\n",
			toggle.asPlot
		);
	}

	test_a_toggle_that_has_inverted_values_should_have_correct_led_lit {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.value = 3;
		toggle.valuesAreInverted = true;

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 L - - -\n",
			toggle.asPlot
		);
	}

	test_when_toggles_value_is_set_inverted_all_leds_should_automatically_refresh {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener;
		toggle.id = \abc;
		toggle.valuesAreInverted = false;
		toggle.value = 3;
		listener = MockViewLedRefreshedListener.new(toggle);

		toggle.valuesAreInverted = true;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: true ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(2, 0), on: false ),
					( source: \abc, point: Point.new(3, 0), on: false ),
				]
			)
		);
	}

	test_when_toggles_value_is_set_inverted_all_pressed_view_buttons_should_be_released {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.valuesAreInverted = false;
		toggle.press(Point.new(2, 0));

		toggle.valuesAreInverted = true;

		this.assert(toggle.allReleased);
	}

	test_when_toggles_value_is_set_not_inverted_all_leds_should_automatically_refresh {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener;
		toggle.id = \abc;
		toggle.valuesAreInverted = true;
		toggle.value = 3;
		listener = MockViewLedRefreshedListener.new(toggle);

		toggle.valuesAreInverted = false;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(2, 0), on: false ),
					( source: \abc, point: Point.new(3, 0), on: true ),
				]
			)
		);
	}

	test_when_toggles_value_is_set_not_inverted_all_pressed_view_buttons_should_be_released {
		var toggle = GRHToggle.newDetached(4, 1);
		toggle.valuesAreInverted = true;
		toggle.press(Point.new(2, 0));

		toggle.valuesAreInverted = false;

		this.assert(toggle.allReleased);
	}

	// decoupled toggle behavior
	test_a_decoupled_toggle_should_not_change_value_nor_trigger_main_action_when_it_is_pressed_nor_released {
		var toggle = GRHToggle.newDecoupled(nil, nil, 4, 1);
		var listener = MockActionListener.new(toggle);

		this.assertEqual(0, toggle.value);
		toggle.press(Point.new(0, 0));
		this.assertEqual(0, toggle.value);
		toggle.press(Point.new(1, 0));
		this.assertEqual(0, toggle.value);
		toggle.release(Point.new(0, 0));
		this.assertEqual(0, toggle.value);
		toggle.release(Point.new(1, 0));
		this.assertEqual(0, toggle.value);

		this.assert( listener.hasNotBeenNotifiedOfAnything );
	}

	// coupled toggle behavior
	test_a_coupled_toggle_should_change_value_every_time_it_is_pressed {
		var toggle = GRHToggle.newDetached(4, 1);

		toggle.press(Point.new(3, 0));
		this.assertEqual(3, toggle.value);
		toggle.press(Point.new(1, 0));
		this.assertEqual(1, toggle.value);
		toggle.press(Point.new(2, 0));
		this.assertEqual(2, toggle.value);
	}

	test_a_coupled_toggle_should_trigger_the_main_action_every_time_it_is_pressed {
		var toggle = GRHToggle.newDetached(4, 1);
		var listener = MockActionListener.new(toggle);

		toggle.press(Point.new(3, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[toggle, 3]
				]
			)
		);

		toggle.press(Point.new(1, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[toggle, 3],
					[toggle, 1]
				]
			)
		);

		toggle.press(Point.new(2, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[toggle, 3],
					[toggle, 1],
					[toggle, 2]
				]
			)
		);

	}

	// nillable toggle behavior
	test_a_nillable_toggle_should_have_its_value_set_to_nil_if_it_is_pressed_on_the_button_equivalent_to_the_value_it_currently_has {
		var toggle = GRHToggle.newNillable(nil, nil, 4, 1);
		toggle.value = 3;

		toggle.press(Point.new(3, 0));

		this.assertEqual(nil, toggle.value);
	}

	test_a_nillable_toggle_should_trigger_the_main_action_when_its_value_is_set_to_nil {
		var toggle = GRHToggle.newNillable(nil, nil, 4, 1);
		var listener;
		toggle.value = 3;
		listener = MockActionListener.new(toggle);

		toggle.press(Point.new(3, 0));

		this.assert( listener.hasBeenNotifiedOf( [ [toggle, nil] ] ) );
	}

	test_when_a_nillable_toggle_with_value_nil_is_set_not_nillable_it_should_get_value_0 {
		var toggle = GRHToggle.newNillable(nil, nil, 4, 1);
		toggle.value = nil;

		toggle.nillable = false;

		this.assertEqual(0, toggle.value);
	}
}
