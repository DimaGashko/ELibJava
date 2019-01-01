package com.windows;

import com.controllers.CreateBook;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowCreateBook extends BaseWindow<CreateBook> {
    private String title = "Create New Book";

    public WindowCreateBook() throws IOException {
        super();

        setTitle(title);
    }

    public WindowCreateBook(String title) throws IOException {
        super();

        setTitle(title);
    }

    public void setTitle(String title) {
        this.title = title;

        controller.setTitle(title);
        window.setTitle(title);
    }

    @Override
    protected void init() throws IOException {
        super.init();

        initEvents();
    }

    protected void load() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("../views/createBook.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        controller = loader.getController();

        window.setScene(scene);

        Image icon = new Image(getClass().getResource("../img/icon.png").toString());
        window.getIcons().add(icon);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(500);
        window.setMinHeight(680);
    }

    private void initEvents() {
        controller.setOnCancelHandler(() -> window.hide());
    }

    private String getTitle() {
        return title;
    }
}
