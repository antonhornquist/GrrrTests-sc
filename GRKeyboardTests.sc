GRKeyboardTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// Initialization

	test_a_keyboard_should_by_default_be_7x2_coupled_have_basenote_60_and_indicate_both_black_and_white_keys {
		var keyboard = GRKeyboard.newDetached;
		this.assertEqual(7, keyboard.numCols);
		this.assertEqual(2, keyboard.numRows);
		this.assert(keyboard.isCoupled);
		this.assertEqual(60, keyboard.basenote);
		this.assertEqual(\blackAndWhite, keyboard.indicateKeys);
	}

	test_it_should_be_possible_to_create_a_decoupled_keyboard {
		this.assertNoErrorThrown {
			GRKeyboard.newDecoupled(nil, nil, 7, 72)
		};
	}

	// Basenote and keyrange

/*
	test_it_should_only_be_possible_to_create_a_keyboard_with_a_basenote_that_is_a_white_key_on_the_keyboard {
		this.assertNoErrorThrown {
			GRKeyboard.newDetached(7, 72)
		};
		assert_raise(RuntimeError) {
			GRKeyboard.newDetached(7, 73)
		};
	}
*/

	test_it_should_be_possible_to_change_basenote_of_a_keyboard {
		var keyboard = GRKeyboard.newDetached(7, 60);
		
		keyboard.basenote = 62;

		this.assertEqual(62, keyboard.basenote);
	}

/*
	test_it_should_not_be_possible_to_change_basenote_to_a_note_that_is_not_a_white_key_on_the_keyboard {
		var keyboard = GRKeyboard.newDetached(7, 60);
		
		assert_raise(RuntimeError) {
			keyboard.basenote = 61
		};
	}
*/

	test_when_basenote_property_is_changed_pressed_buttons_on_keyboard_view_should_be_released {
		var keyboard = GRKeyboard.newDetached(7, 60);
		keyboard.press(Point.new(0, 1));
		keyboard.press(Point.new(2, 0));
		keyboard.press(Point.new(4, 1));

		keyboard.basenote = 62;
		
		this.assert(keyboard.allReleased);
	}

/*
	test_when_basenote_is_changed_the_keyboard_should_automatically_refresh {
		var keyboard = GRKeyboard.newDetached(7, 60);
		var listener = MockViewLedRefreshedListener.new(keyboard);
		keyboard.id = \keyboard;

		keyboard.basenote = 62;
		
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \keyboard, point: Point.new(0, 0), on: true ),
					( source: \keyboard, point: Point.new(1, 0), on: true ),
					( source: \keyboard, point: Point.new(2, 0), on: false ),
					( source: \keyboard, point: Point.new(3, 0), on: true ),
					( source: \keyboard, point: Point.new(4, 0), on: true ),
					( source: \keyboard, point: Point.new(5, 0), on: true ),
					( source: \keyboard, point: Point.new(6, 0), on: false ),
					( source: \keyboard, point: Point.new(0, 1), on: true ),
					( source: \keyboard, point: Point.new(1, 1), on: true ),
					( source: \keyboard, point: Point.new(2, 1), on: true ),
					( source: \keyboard, point: Point.new(3, 1), on: true ),
					( source: \keyboard, point: Point.new(4, 1), on: true ),
					( source: \keyboard, point: Point.new(5, 1), on: true ),
					( source: \keyboard, point: Point.new(6, 1), on: true ),
				]
			)
		);
	}

	test_it_should_be_possible_to_retrieve_the_keyrange_of_a_keyboard {
		var keyboard = GRKeyboard.newDetached(3, 62);
		this.assertEqual(
			(61..65),
			keyboard.keyrange
		);
	}

	test_it_should_be_possible_to_retrieve_the_number_of_notes_in_the_keyrange_of_a_keyboard {
		var keyboard = GRKeyboard.newDetached(4, 60);

		this.assertEqual(
			6,
			keyboard.num_notes
		);
	}

	test_the_value_of_a_keyboard_should_be_an_array_of_boolean_values_corresponding_to_how_notes_are_displayed {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.press(Point.new(2, 1));
		keyboard.press(Point.new(3, 0));

		this.assert(
			[false, false, false, false, true, true, false, false, false, false, false, false, false],
			keyboard.value
		);
	}

	test_when_a_note_is_displayed_as_pressed_on_a_keyboard_its_corresponding_led_should_be_inverted_from_its_normal_led_state {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.displayNoteAsPressed(64);

		this.assertEqual(
			true,
			keyboard.isUnlitAt(Point.new(2, 1))
		);
	}

	// Note press / release events and state

	test_when_a_button_on_the_keyboard_view_that_corresponds_to_a_note_is_pressed_the_state_should_be_reflected {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.press(Point.new(2, 1));

		this.assert(keyboard.noteIsPressed(64));
	}

	test_when_a_button_on_the_keyboard_view_that_corresponds_to_a_note_that_currently_is_pressed_is_released_the_state_should_be_reflected {
		var keyboard = GRKeyboard.newDetached(7, 60);
		keyboard.press(Point.new(2, 1));

		keyboard.release(Point.new(2, 1));

		this.assert(keyboard.noteIsReleased(64));
	}

	test_it_should_be_possible_to_get_notified_of_notes_being_pressed_on_view_by_adding_an_action_to_a_keyboard {
		var keyboard = GRKeyboard.newDetached(7, 60);
		var listener = MockNotePressedListener.new(keyboard);

		keyboard.press(Point.new(2, 1));
		keyboard.press(Point.new(4, 1));
		keyboard.press(Point.new(1, 0));
		keyboard.release(Point.new(4, 1));
		keyboard.release(Point.new(1, 0));
		keyboard.release(Point.new(2, 1));

		this.assert(
			listener.hasBeenNotifiedOf( [[keyboard, 64], [keyboard, 67], [keyboard, 61]] )
		);
	}

	test_it_should_be_possible_to_get_notified_of_notes_getting_released_on_view_by_adding_an_action_to_a_keyboard {
		var keyboard = GRKeyboard.newDetached(7, 60);
		var listener = MockNoteReleasedListener.new(keyboard);

		keyboard.press(Point.new(2, 1));
		keyboard.press(Point.new(4, 1));
		keyboard.press(Point.new(1, 0));
		keyboard.release(Point.new(4, 1));
		keyboard.release(Point.new(1, 0));
		keyboard.release(Point.new(2, 1));

		this.assert(
			listener.hasBeenNotifiedOf( [[keyboard, 67], [keyboard, 61], [keyboard, 64]] )
		);
	}

	test_it_should_be_possible_to_determine_in_what_order_currently_pressed_notes_on_a_keyboard_have_been_pressed_on_the_view {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.press(Point.new(2, 1));
		keyboard.press(Point.new(4, 1));
		keyboard.press(Point.new(1, 0));
		keyboard.release(Point.new(4, 1));
		keyboard.press(Point.new(4, 1));

		this.assertEqual([64, 61, 67], keyboard.notes_pressed);
	}

	// Note display

	test_it_should_be_possible_to_display_notes_as_pressed_on_a_decoupled_keyboard {
		var keyboard = GRKeyboard.newDecoupled(nil, nil, 7, 60);

		keyboard.displayNoteAsPressed(61);

		this.assert(
			keyboard.noteIsDisplayedAsPressed(61)
		);
	}

	test_it_should_be_possible_to_display_notes_as_released_on_a_decoupled_keyboard {
		var keyboard = GRKeyboard.newDecoupled(nil, nil, 7, 60);
		keyboard.displayNoteAsPressed(61);

		keyboard.display_note_as_released(61);

		this.assert(
			keyboard.noteIsDisplayedAsReleased(61)
		);
	}

	// Coupling and decoupling

	test_a_coupled_keyboard_should_update_note_display_state {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.press(Point.new(2, 1));

		this.assert(keyboard.noteIsDisplayedAsPressed(64));

		keyboard.release(Point.new(2, 1));

		this.assert(keyboard.noteIsDisplayedAsReleased(64));
	}

	test_a_decoupled_keyboard_should_not_update_display_state {
		var keyboard = GRKeyboard.newDecoupled(nil, nil, 7, 60);

		keyboard.press(Point.new(2, 1));

		this.assert(keyboard.noteIsDisplayedAsReleased(64));

		keyboard.displayNoteAsPressed(64);
		keyboard.release(Point.new(2, 1));

		this.assert(keyboard.noteIsDisplayedAsPressed(64));
	}

	test_when_a_keyboard_is_set_decoupled_and_vice_versa_all_pressed_buttons_on_view_should_be_released {
		var keyboard = GRKeyboard.newDetached(7, 60);
		keyboard.press(Point.new(0, 1));

		keyboard.coupled = false;
		
		this.assert(
			keyboard.allReleased
		);

		keyboard.press(Point.new(0, 1));

		keyboard.coupled = true;
		
		this.assert(
			keyboard.allReleased
		);
	}

	// Indicate keys

	test_when_indicate_keys_value_value_black_and_white_leds_of_all_keyboard_keys_should_be_lit {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.indicateKeys = \blackAndWhite;

		this.assertEqual(
			"  0 1 2 3 4 5 6      0 1 2 3 4 5 6\n" +
			"0 - - - - - - -    0 - L L - L L L\n" +
			"1 - - - - - - -    1 L L L L L L L\n",
			keyboard.asPlot
		);
	}

	test_when_indicate_keys_property_is_black_leds_of_all_black_keyboard_keys_should_be_lit {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.indicateKeys = \black;

		this.assertEqual(
			"  0 1 2 3 4 5 6      0 1 2 3 4 5 6\n" +
			"0 - - - - - - -    0 - L L - L L L\n" +
			"1 - - - - - - -    1 - - - - - - -\n",
			keyboard.asPlot
		);
	}

	test_when_indicate_keys_property_is_white_leds_of_all_white_keyboard_keys_should_be_lit {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.indicateKeys = \white;

		this.assertEqual(
			"  0 1 2 3 4 5 6      0 1 2 3 4 5 6\n" +
			"0 - - - - - - -    0 - - - - - - -\n" +
			"1 - - - - - - -    1 L L L L L L L\n",
			keyboard.asPlot
		);
	}

	test_when_indicate_keys_property_is_none_no_leds_should_be_lit {
		var keyboard = GRKeyboard.newDetached(7, 60);

		keyboard.indicateKeys = \none;

		this.assertEqual(
			"  0 1 2 3 4 5 6      0 1 2 3 4 5 6\n" +
			"0 - - - - - - -    0 - - - - - - -\n" +
			"1 - - - - - - -    1 - - - - - - -\n",
			keyboard.asPlot
		);
	}

	test_when_indicate_keys_property_is_changed_the_keyboard_should_automatically_refresh {
		var keyboard = GRKeyboard.newDetached(7, 60);
		var listener = MockViewLedRefreshedListener.new(keyboard);
		keyboard.id = \keyboard;

		keyboard.indicateKeys = \none;
		
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \keyboard, point: Point.new(0, 0), on: false ),
					( source: \keyboard, point: Point.new(1, 0), on: false ),
					( source: \keyboard, point: Point.new(2, 0), on: false ),
					( source: \keyboard, point: Point.new(3, 0), on: false ),
					( source: \keyboard, point: Point.new(4, 0), on: false ),
					( source: \keyboard, point: Point.new(5, 0), on: false ),
					( source: \keyboard, point: Point.new(6, 0), on: false ),
					( source: \keyboard, point: Point.new(0, 1), on: false ),
					( source: \keyboard, point: Point.new(1, 1), on: false ),
					( source: \keyboard, point: Point.new(2, 1), on: false ),
					( source: \keyboard, point: Point.new(3, 1), on: false ),
					( source: \keyboard, point: Point.new(4, 1), on: false ),
					( source: \keyboard, point: Point.new(5, 1), on: false ),
					( source: \keyboard, point: Point.new(6, 1), on: false ),
				]
			)
		);
	}
*/
}
