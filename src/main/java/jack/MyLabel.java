package jack;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MyLabel extends Label {
    private final String name;
    private Manager man = Manager.getInstance();
    private TextArea read_area;

    boolean isBold;
    boolean isActive;
    boolean isSelected;


    private final static String DEFAULTSTYLE = "-fx-background-color: #d8e1ff; -fx-padding: 1px; ";


    public MyLabel(String name, TextArea readArea) {
        super();
        StringBuilder v = new StringBuilder(name);
        v.deleteCharAt(0);
        this.name = (name.charAt(0)+"").toUpperCase() + v.toString();
        setText(this.name);
        this.read_area = readArea;

        setOnMouseClicked(s -> {
            Manager.getInstance().userData.selectUser(name.toLowerCase());
            setSelected(true);
            Lo.g("Selected: " + name.toLowerCase());

            read_area.clear();
            for (String msg : man.userData.getConversationWith(name.toLowerCase())) read_area.appendText(msg + "\n");
            setBold(false);
        });
        man.userData.allLabels.add(this);




        setFont(new Font("Helvetica", 20));
        setOpacity(0.3);
        setAlignment(Pos.TOP_CENTER);
        setMaxHeight(1.7976931348623157E308);
        setMaxWidth(1.7976931348623157E308);
        setStyle(DEFAULTSTYLE);
    }

    public void setSelected(boolean selected) {
        if(selected) {
            setOpacity(1);
            isSelected = true;
        } else {
            setOpacity(0.3);
            isSelected = false;
        }

    }

    public void setBold(boolean bold) {
        if(bold) {
            //setStyle(DEFAULTSTYLE + "-fx-font-weight: 900;");
            setFont(Font.font("null", FontWeight.BOLD, 20));
            setUnderline(true);
            isBold = true;
        } else {
            //setStyle(DEFAULTSTYLE + "-fx-font-weight: normal;");
            setFont(Font.font("null", FontWeight.NORMAL, 20));
            setUnderline(false);
            isBold = false;
        }
    }

    public String getName() {
        return name;
    }


    public void setActive(boolean active) {
        if(active) {
            //setStyle(DEFAULTSTYLE + "-fx-text-fill: #1eff00; ");
            setTextFill(Color.web("#1eff00"));
            isActive = true;
        } else {
            //setStyle(DEFAULTSTYLE + "-fx-text-fill: #a52a2a;");
            setTextFill(Color.web("#a52a2a"));
            isActive = false;
        }
    }
}
