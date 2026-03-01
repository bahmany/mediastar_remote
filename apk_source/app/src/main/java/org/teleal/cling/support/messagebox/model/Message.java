package org.teleal.cling.support.messagebox.model;

import android.support.v7.internal.widget.ActivityChooserView;
import java.util.Random;
import org.teleal.cling.support.messagebox.parser.MessageDOM;
import org.teleal.cling.support.messagebox.parser.MessageDOMParser;
import org.teleal.cling.support.messagebox.parser.MessageElement;
import org.teleal.common.xml.ParserException;

/* loaded from: classes.dex */
public abstract class Message implements ElementAppender {
    private final Category category;
    private DisplayType displayType;
    private final int id;
    protected final Random randomGenerator;

    public enum Category {
        SMS("SMS"),
        INCOMING_CALL("Incoming Call"),
        SCHEDULE_REMINDER("Schedule Reminder");

        public String text;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Category[] valuesCustom() {
            Category[] categoryArrValuesCustom = values();
            int length = categoryArrValuesCustom.length;
            Category[] categoryArr = new Category[length];
            System.arraycopy(categoryArrValuesCustom, 0, categoryArr, 0, length);
            return categoryArr;
        }

        Category(String text) {
            this.text = text;
        }
    }

    public enum DisplayType {
        MINIMUM("Minimum"),
        MAXIMUM("Maximum");

        public String text;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static DisplayType[] valuesCustom() {
            DisplayType[] displayTypeArrValuesCustom = values();
            int length = displayTypeArrValuesCustom.length;
            DisplayType[] displayTypeArr = new DisplayType[length];
            System.arraycopy(displayTypeArrValuesCustom, 0, displayTypeArr, 0, length);
            return displayTypeArr;
        }

        DisplayType(String text) {
            this.text = text;
        }
    }

    public Message(Category category, DisplayType displayType) {
        this(0, category, displayType);
    }

    public Message(int id, Category category, DisplayType displayType) {
        this.randomGenerator = new Random();
        this.id = id == 0 ? this.randomGenerator.nextInt(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) : id;
        this.category = category;
        this.displayType = displayType;
    }

    public int getId() {
        return this.id;
    }

    public Category getCategory() {
        return this.category;
    }

    public DisplayType getDisplayType() {
        return this.displayType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return this.id == message.id;
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        try {
            MessageDOMParser mp = new MessageDOMParser();
            MessageDOM dom = (MessageDOM) mp.createDocument();
            MessageElement root = dom.createRoot(mp.createXPath(), "Message");
            ((MessageElement) root.createChild("Category")).setContent(getCategory().text);
            ((MessageElement) root.createChild("DisplayType")).setContent(getDisplayType().text);
            appendMessageElements(root);
            String s = mp.print(dom, 0, false);
            return s.replaceAll("<Message xmlns=\"urn:samsung-com:messagebox-1-0\">", "").replaceAll("</Message>", "");
        } catch (ParserException ex) {
            throw new RuntimeException((Throwable) ex);
        }
    }
}
