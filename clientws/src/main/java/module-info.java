module fr.univ.orleans.pnt {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens fr.univ.orleans.pnt to javafx.fxml;
    exports fr.univ.orleans.pnt;
    exports fr.univ.orleans.pnt.vues;
    exports fr.univ.orleans.pnt.modele.dtos to com.fasterxml.jackson.databind;
    opens fr.univ.orleans.pnt.vues to javafx.fxml;
    opens fr.univ.orleans.pnt.modele.dtos to com.fasterxml.jackson.databind;
}