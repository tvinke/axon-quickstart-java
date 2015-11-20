package example;

import java.io.File;
import java.util.UUID;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;

public class ToDoItemRunner {

	public static void main(String[] args) {
        // let's start with the Command Bus
        CommandBus commandBus = new SimpleCommandBus();
 
        // the CommandGateway provides a friendlier API
        CommandGateway commandGateway = new DefaultCommandGateway(commandBus);
 
        // we'll store Events on the FileSystem, in the "events/" folder
        EventStore eventStore = new FileSystemEventStore(new SimpleEventFileResolver(new File("./events")));
 
        // a Simple Event Bus will do
        EventBus eventBus = new SimpleEventBus();
 
        // we need to configure the repository
        EventSourcingRepository repository = new EventSourcingRepository(ToDoItem.class, eventStore);
        repository.setEventBus(eventBus);
 
        // Axon needs to know that our ToDoItem Aggregate can handle commands
        AggregateAnnotationCommandHandler.subscribe(ToDoItem.class, repository, commandBus);
 
        // Axon will wrap the bean into an Event Listener and invoke the annotated methods when Events are received through the Event Bus passed as the second parameter
        AnnotationEventListenerAdapter.subscribe(new ToDoEventHandler(), eventBus);
        
        // and let's send some Commands on the CommandBus.
        final String itemId = UUID.randomUUID().toString();
        commandGateway.send(new CreateToDoItemCommand(itemId, "Need to do this"));
        commandGateway.send(new MarkCompletedCommand(itemId));
    }
}
