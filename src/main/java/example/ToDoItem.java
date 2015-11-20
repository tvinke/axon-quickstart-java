package example;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

public class ToDoItem<I> extends AbstractAnnotatedAggregateRoot<I> {

	private static final long serialVersionUID = -3561350815284207201L;
	
	@AggregateIdentifier
    private String id;
	
	public ToDoItem() {
		// default no-args ctor
	}
	
	@CommandHandler
    public ToDoItem(CreateToDoItemCommand command) {
        apply(new ToDoItemCreatedEvent(command.getTodoId(), command.getDescription()));
    }
	
	@EventHandler
    public void on(ToDoItemCreatedEvent event) {
        this.id = event.getTodoId();
    }
	
	@CommandHandler
	public void markCompleted(MarkCompletedCommand command) {
	    apply(new ToDoItemCompletedEvent(id));
	}
}
