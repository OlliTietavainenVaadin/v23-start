package com.example.application.views.masterdetail;

import com.example.application.data.entity.SamplePerson;
import com.example.application.data.service.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Master-Detail")
@Route(value = "master-detail/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";

    private Grid<SamplePerson> grid = new Grid<>(SamplePerson.class);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private TextField occupation;
    private Checkbox important;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private SamplePerson samplePerson;

    private SamplePersonService samplePersonService;

    public MasterDetailView(@Autowired SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("master-detail-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        Binder<SamplePerson> gridBinder = new Binder<>(SamplePerson.class);

        TextField textField = new TextField("First name");
        grid.getColumnByKey("firstName").setEditorComponent(textField);

        ComboBox<String> comboBox = new ComboBox<>("Important");
        comboBox.setItems("Yes", "No");
        comboBox.addValueChangeListener( e -> {
            if (!e.isFromClient()) {
                Notification.show("Chose " + e.getValue());
                comboBox.clear();
            }
        });

        grid.getColumnByKey("important").setEditorComponent(comboBox);

        gridBinder.forField(comboBox).
                withConverter(new Converter<String, Boolean>() {
                    @Override
                    public Result<Boolean> convertToModel(String s, ValueContext valueContext) {
                        return Result.ok("Yes".equals(s));
                    }

                    @Override
                    public String convertToPresentation(Boolean aBoolean, ValueContext valueContext) {
                        return aBoolean ? "Yes" : "No";
                    }
                })
                .withValidator(bool -> bool == false, "Can't be that important")
                .bind("important");

        gridBinder.forField(textField)
                .withValidator(string -> string.length() > 2, "Name must be longer")
                .bind("firstName");

        grid.getEditor().setBinder(gridBinder);
        grid.getEditor().setBuffered(true);

        Grid.Column<SamplePerson> editColumn = grid.addComponentColumn(bean -> new Button("Edit", e -> {
            samplePerson = bean;
            grid.getEditor().editItem(bean);
        }));


        
        editColumn.setEditorComponent(new Button("Save", e -> {
            try {
                gridBinder.writeBean(samplePerson);
            } catch (ValidationException ex) {
                // validation failed
                return;
            }
            grid.getEditor().save();
        }));

        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


        grid.setHeightFull();


    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    private void createEditorLayout(SplitLayout splitLayout) {

    }

    private void createButtonLayout(Div editorLayoutDiv) {

    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {

    }

    private void populateForm(SamplePerson value) {

    }
}
