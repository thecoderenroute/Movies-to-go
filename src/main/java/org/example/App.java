package org.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import org.json.JSONArray;
import org.json.JSONObject;

public class App extends Application {

    public static void main(String[] args) {
        launch(App.class);
    }


    @Override
    public void start(Stage stage) throws Exception {

        BorderPane bp = new BorderPane();

        GridPane layout = new GridPane();

        final Mode[] current_mode = {Mode.i};

        layout.setHgap(10);
        layout.setVgap(20);

        Label hello = new Label("Enter ID:");
        hello.setMinWidth(70);
        TextField getInput = new TextField();

        HBox hb = new HBox();
        hb.setSpacing(10);
        hb.setPadding(new Insets(0, 10, 0, 10));

        ToggleGroup tg = new ToggleGroup();

        RadioButton r1 = new RadioButton("ID");
        RadioButton r2 = new RadioButton("Title");
        RadioButton r3 = new RadioButton("Search");

        r1.setToggleGroup(tg);
        r2.setToggleGroup(tg);
        r3.setToggleGroup(tg);

        r1.setSelected(true);

        tg.selectedToggleProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {

                RadioButton rb = (RadioButton) tg.getSelectedToggle();
                String text = rb.getText();
                hello.setText("Enter " + text + ":");
                current_mode[0] = Mode.valueOf(text.toLowerCase().substring(0, 1));
            }
        });
        Button b = new Button("Submit");

        hb.getChildren().addAll(r1, r2, r3);

        layout.add(hello, 0, 0);
        layout.add(getInput, 1, 0);
        layout.add(hb, 0, 1, 2, 1);
        layout.add(b, 1, 2, 2, 1);

        bp.setCenter(layout);
        layout.setAlignment(Pos.CENTER);

        b.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    System.out.println(getMovies(current_mode[0], getInput.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(bp, 300, 350);

        stage.setScene(scene);
        stage.setTitle("Movies API");
        stage.show();

    }

    public List<Movie> getMovies(Mode mode, String query) throws Exception {

        List<Movie> movies = new ArrayList<>();

        String host = "http://www.omdbapi.com/";

        String apikey = "8a0e22ef";
        String charset = "UTF-8";

        char c = mode.toString().charAt(0);
        String q = String.format(c + "=%s", URLEncoder.encode(query, charset));

        String url = host + "?apikey=" + apikey + "&" + q;

        System.out.println(url);
        System.out.println("c is " + c);

        HttpResponse<JsonNode> response = Unirest.get(host + "?apikey=" + apikey + "&" + q).asJson();

        JSONArray arr = response.getBody().getArray();

        System.out.println("Array is: " + arr.toString());

        if (mode == Mode.i || mode == Mode.t) {

            for (Object obj : arr) {
                JSONObject jo = (JSONObject) obj;
                System.out.println("The obj is " + jo.toString());

                try {
                    String name = jo.getString("Title");
                    long year = jo.getLong("Year");
                    String director = jo.getString("Director");
                    String plot = jo.getString("Plot");
                    String rating = jo.getString("imdbRating");

                    Movie m = new Movie(name, year, director, plot, rating);
                    movies.add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            JSONArray arrM = (JSONArray) arr.get(2);
            for (Object obj : arrM) {
                JSONObject jo = (JSONObject) obj;

                if (jo.get("Type") == "movie") {
                    String id = jo.getString("imdbID");
                    String q1 = String.format("i=%s", URLEncoder.encode(id, charset));
                    HttpResponse<JsonNode> temp = Unirest.get(host + "?apikey=" + apikey + "&" + q1).asJson();

                }
            }
        }

        return movies;
    }

}
