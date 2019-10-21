//@@author LL-Pengfei
/**
 * DeleteCommand.java
 * Support commands related to delete.
 */
package cube.logic.command;

import cube.model.FoodList;
import cube.model.Food;
import cube.storage.StorageManager;
import cube.logic.command.exception.CommandException;
import cube.logic.command.exception.CommandErrorMessage;

/**
 * This class supports commands related to delete.
 */
public class DeleteCommand extends Command {
	/**
	 * Use enums to specify the states of the food to be deleted.
	 */
	public enum DeleteBy {
		INDEX, NAME, TYPE
	}

	private int deleteIndex;
	private String deleteDescription;
	private DeleteBy param;
	private final String MESSAGE_SUCCESS_SINGLE = "Nice! I've removed this food:\n"
			+ "%1$s\n"
		    + "Now you have %2$s food in the list.\n";
	private final String MESSAGE_SUCCESS_MULTIPLE = "Nice! I've removed this type:\n"
			+ "%1$s\n"
			+ "This type contains "
			+ "%2$s food items\n"
			+ "Now you have %3$s food in the list.\n";

	/**
	 * The default constructor, empty since parameters are required to perform delete command.
	 */
	public DeleteCommand() {
	}

	/**
	 * The constructor for delete using index.
	 *
	 * @param index The index of the food to be deleted.
	 * @param param The parameter is used to specify the type of deletion.
	 */
	public DeleteCommand(int index, String param) {
		this.deleteIndex = index - 1;
		this.param = DeleteBy.valueOf(param);
	}

	/**
	 * The constructor for delete using food name or food type.
	 *
	 * @param deleteDescription The food name or food type to be deleted.
	 * @param param The parameter is used to specify the type of deletion.
	 */
	public DeleteCommand(String deleteDescription, String param) {
		this.deleteDescription = deleteDescription;
		this.param = DeleteBy.valueOf(param);
	}

	/**
	 * The class checks whether a given index is valid or not.
	 *
	 * @param list The food list.
	 * @throws CommandException If the given index is invalid.
	 */
	private void checkValidIndex(FoodList list) throws CommandException {
		if (deleteIndex < 0 || deleteIndex >= list.size()) {
			throw new CommandException(CommandErrorMessage.FOOD_NOT_EXISTS);
		}
	}

	/**
	 * The class checks whether a given food name is in the food list or not.
	 *
	 * @param list The food list.
	 * @throws CommandException If the given food name is not inside the food list.
	 */
	private void checkValidName(FoodList list) throws CommandException {
		if (!list.existsName(deleteDescription)) {
			throw new CommandException(CommandErrorMessage.FOOD_NOT_EXISTS);
		}
	}

	/**
	 * The class checks whether a given food type is in the food list or not.
	 * @param list The food list.
	 * @throws CommandException If the given food type is not inside the food list.
	 */
	private void checkValidType(FoodList list) throws CommandException {
		if (!list.existsType(deleteDescription)) {
			throw new CommandException(CommandErrorMessage.FOOD_NOT_EXISTS);
		}
	}

	/**
	 * The class removes the food the user wishes to remove.
	 *
	 * @param list The food list.
	 * @param storage The storage we have.
	 * @return The Feedback to User for Delete Command.
	 * @throws CommandException If deletion is unsuccessful.
	 */
	@Override
	public CommandResult execute(FoodList list, StorageManager storage) throws CommandException {
		Food toDelete;
		switch (param) {
			case INDEX:
				checkValidIndex(list);
				toDelete = list.get(deleteIndex);
				list.removeIndex(deleteIndex);
				storage.storeFoodList(list);
				return new CommandResult(String.format(MESSAGE_SUCCESS_SINGLE, toDelete, list.size()));
			case NAME:
				checkValidName(list);
				toDelete = list.get(deleteDescription);
				list.removeName(deleteDescription);
				storage.storeFoodList(list);
				return new CommandResult(String.format(MESSAGE_SUCCESS_SINGLE, toDelete, list.size()));
			case TYPE:
				checkValidType(list);
				int count = list.removeType(deleteDescription);
				storage.storeFoodList(list);
				return new CommandResult(String.format(MESSAGE_SUCCESS_MULTIPLE, deleteDescription, count, list.size()));
		}
		return null;
	}
}