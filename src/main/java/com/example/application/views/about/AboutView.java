package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    private boolean openToggled = true;

    public AboutView() {
        setSpacing(false);
        MenuBar menuBar = new MenuBar();

        MenuItem open = menuBar.addItem("Open");
        MenuItem close = menuBar.addItem("Close");

        open.addClickListener(event -> {
            close.getElement().getThemeList().set("toggled", false);
            open.getElement().getThemeList().set("toggled", true);
        });

        close.addClickListener(event -> {
            open.getElement().getThemeList().set("toggled", false);
            close.getElement().getThemeList().set("toggled", true);
        });
        add(menuBar);
    }

}
