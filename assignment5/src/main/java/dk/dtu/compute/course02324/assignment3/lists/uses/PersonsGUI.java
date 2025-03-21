package dk.dtu.compute.course02324.assignment3.lists.uses;


import dk.dtu.compute.course02324.assignment3.lists.implementations.GenericComparator;

import java.util.List;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A GUI element that is allows the user to interact and
 * change a list of persons.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class PersonsGUI extends GridPane {

    /**
     * The list of persons to be maintained in this GUI.
     */
    final private List<Person> persons;

    private GridPane personsPane;

    private int weightCount = 1;

    /**
     Initialize average, most occuring and exception label, to handle updated values later.
     */
    private Label averageLabel = new Label("Average weight is 0.00kg");
    private Label mostOccuringLabel = new Label("Most occuring: " + "");
    private Label exceptionLabel = new Label("");

    /**
     * Constructor which sets up the GUI attached a list of persons.
     *
     * @param persons the list of persons which is to be maintained in
     *                this GUI component; it must not be <code>null</code>
     */
    public PersonsGUI(@NotNull List<Person> persons) {
        this.persons = persons;

        this.setVgap(5.0);
        this.setHgap(5.0);

        // text filed for user entering a name
        TextField field = new TextField();
        field.setPrefColumnCount(5);

        // weight filed for user entering a name
        TextField weightField = new TextField();
        weightField.setPrefColumnCount(5);

        // weight filed for user entering a name
        TextField ageField = new TextField();
        ageField.setPrefColumnCount(5);

        // given Index filed for user entering a name
        TextField givenIndexField = new TextField();
        givenIndexField.setPrefColumnCount(5);

        // button for adding a new person to the list (based on
        // the name in the text field (the given weight)
        Button addButton = new Button("Add at the end of list");
        addButton.setOnAction(
                e -> {
                    try{
                        Person person = new Person(field.getText(), Double.parseDouble(weightField.getText()), Integer.parseInt(ageField.getText()));
                        //Person person = new Person(field.getText(), weightCount++);
                        persons.add(person);
                        // makes sure that the GUI is updated accordingly
                        update();
                    }catch(Exception err){
                        exceptionLabel.setText("Exception: " + err);
                        System.out.println("Exception" + err);
                    }
                });

        //Adds on a given index
        Button AddOnIndexButton = new Button("Add at index: ");
        AddOnIndexButton.setOnAction(
                e -> {
                    try{
                        //Define new person
                        Person person = new Person(field.getText(), Double.parseDouble(weightField.getText()),Integer.parseInt(ageField.getText()));
                        //Add at given index
                        persons.add(Integer.parseInt(givenIndexField.getText()), person);
                        update();
                    }
                    catch(Exception err){
                        exceptionLabel.setText("Exception: " + err);
                        System.out.println(err);
                    }
                });


        Comparator<Person> comparator = new GenericComparator<>();

        // button for sorting the list (according to the order of Persons,
        // which implement the interface Comparable, which is converted
        // to a Comparator by the GenericComparator above)
        Button sortButton = new Button("Sort");
        sortButton.setOnAction(
                e -> {
                    try{
                        persons.sort(comparator);
                        // makes sure that the GUI is updated accordingly
                        update();
                    }catch(Exception err){
                        exceptionLabel.setText("Exception: " + err);
                        System.out.println(err);
                    }

                });

        // button for clearing the list
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(
                e -> {
                    persons.clear();
                    // makes sure that the GUI is updated accordingly
                    update();
                });

        // combines the above elements into vertically arranged boxes
        // which are then added to the left column of the grid pane

        //Average & Most occuring name
        VBox averageAndMostOccBox = new VBox(averageLabel, mostOccuringLabel);
        averageAndMostOccBox.setSpacing(5.0);

        //Name & Weight Field box UI
            //Name
            Label labelName = new Label("Name");
            VBox nameBox = new VBox(labelName, field);
            //Weight
            Label labelWeight = new Label("Weight");
            VBox weightBox = new VBox(labelWeight, weightField);
            //Combined
            HBox nameWeightBox = new HBox(nameBox, weightBox);
            nameWeightBox.setSpacing(5.0);

        //At given indedx UI
            HBox atGivenIndexBox = new HBox(AddOnIndexButton, givenIndexField);
            atGivenIndexBox.setSpacing(5.0);

        //Main vertical box (left)
        VBox actionBox = new VBox(nameWeightBox, addButton, atGivenIndexBox, sortButton, clearButton, averageAndMostOccBox);
        actionBox.setSpacing(10.0);
        this.add(actionBox, 0, 0);

        // create the elements of the right column of the GUI
        // (scrollable person list) ...
        Label labelPersonsList = new Label("Persons:");

        personsPane = new GridPane();
        personsPane.setPadding(new Insets(5));
        personsPane.setHgap(5);
        personsPane.setVgap(5);

        ScrollPane scrollPane = new ScrollPane(personsPane);
        scrollPane.setMinWidth(300);
        scrollPane.setMaxWidth(300);
        scrollPane.setMinHeight(200);
        scrollPane.setMaxHeight(200);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // ... and adds these elements to the right-hand columns of
        // the grid pane
        VBox personsList = new VBox(labelPersonsList, scrollPane, exceptionLabel);
        personsList.setSpacing(5.0);
        this.add(personsList, 1, 0);

        // updates the values of the different components with the values from
        // the stack
        update();
    }

    /**
     * Updates the values of the GUI elements with the current values
     * from the list.
     */
    private void update() {
        personsPane.getChildren().clear();
        // adds all persons to the list in the personsPane (with
        // a delete button in front of it)
        for (int i=0; i < persons.size(); i++) {
            Person person = persons.get(i);
            Label personLabel = new Label(i + ": " + person.toString());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(
                    e -> {
                        persons.remove(person);
                        update();
                    }
            );
            HBox entry = new HBox(deleteButton, personLabel);
            entry.setSpacing(5.0);
            entry.setAlignment(Pos.BASELINE_LEFT);
            personsPane.add(entry, 0, i);
        }

        //Clear exceptions
        exceptionLabel.setText("");

        //Update average
        double average = updateAverage();
        averageLabel.setText(String.format("Average weight:\n %.2f kg", average));
        //Update most occuring
        String mostOcc = mostOccuring();
        mostOccuringLabel.setText(String.format("Most occuring name:\n %s", mostOcc));

    }

    /**
    This method is used to find the average weight of persons in the list:
     */
    private double updateAverage(){
        if(persons.isEmpty()){
            return 0.0;
        }

        double sum = 0.0;
        for(var i=0; i<persons.size(); i++){
            sum+=persons.get(i).weight;
        }
        return (sum/persons.size());
    }

    /**
     This method is used to find the name of the most occuring person in the list:
     */
    private String mostOccuring(){
        if(persons.isEmpty()){
            return "None";
        }


        //Keep track of highest count
        int highestOccCount = 0;
        var highestCountName = "None";

        //Iterate through all persons
        for(var i=0; i< persons.size(); i++){
            int occCount = 0;

            //Check occurences in list (compare name)
            for(var j=0; j< persons.size(); j++){
                if(persons.get(i).name.equals(persons.get(j).name)){
                    occCount++;
                }
            }

            //Check if occurs more than highest occurence
            if(occCount>highestOccCount){
                highestOccCount=occCount;
                highestCountName=persons.get(i).name;
            }
        }

        //Return name with most occurences
        return highestCountName;

    }

}
