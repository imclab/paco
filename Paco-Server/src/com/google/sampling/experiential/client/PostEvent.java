/*
* Copyright 2011 Google Inc. All Rights Reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance  with the License.  
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package com.google.sampling.experiential.client;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.sampling.experiential.shared.MapService;
import com.google.sampling.experiential.shared.MapServiceAsync;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostEvent implements EntryPoint {

  private VerticalPanel whatPanel;
  private MapServiceAsync mapService = GWT.create(MapService.class);
  private VerticalPanel formPanel;
  private HashMap<String, Widget> fieldToWidgetMap;
  private int keyValueCounter = 0;

  @Override
  public void onModuleLoad() {
    VerticalPanel p = new VerticalPanel();
    HTML l = new HTML("<h1>Post an Event</h1>");
    p.add(l);
    RootPanel.get().add(p);

    formPanel = new VerticalPanel();
    formPanel.setSpacing(10); 
    RootPanel.get().add(formPanel);
    createEventForm();
  }

  private void createEventForm() {
    fieldToWidgetMap = Maps.newHashMap();
    keyValueCounter = 0;
    formPanel.add(createFormLine("who"));

    DateTimeFormat df = DateTimeFormat.getFormat("yyyyMMdd:HH:mm:ssZ");
    String timeString = df.format(new Date());
    formPanel.add(createFormLine("when", timeString));
    formPanel.add(createFormLine("lat"));
    formPanel.add(createFormLine("lon"));
    formPanel.add(createBooleanFormLine("shared", false));
    formPanel.add(new HTML("Enter at least one key/value pair for the event"));
    whatPanel = new VerticalPanel();
    formPanel.add(whatPanel);
    whatPanel.add(createWhatFormLine("", ""));
    formPanel.add(createKeyValueLineButton());
    formPanel.add(createSubmitButton());
  }

  private Widget createSubmitButton() {
    Button whatButton = new Button("Save Event");
    whatButton.addClickListener(new ClickListener() {

      @Override
      public void onClick(Widget sender) {
        submitEvent();
      }

    });
    return whatButton;
  }

  private void submitEvent() {
    String who = ((TextBox) fieldToWidgetMap.get("who")).getText();
    String when = ((TextBox) fieldToWidgetMap.get("when")).getText();
    String lat = ((TextBox) fieldToWidgetMap.get("lat")).getText();
    String lon = ((TextBox) fieldToWidgetMap.get("lon")).getText();
    boolean shared = ((CheckBox)fieldToWidgetMap.get("shared")).getValue();
    Map<String, String> kvPairs = Maps.newHashMap();
    for (int i = keyValueCounter - 1; i >= 0; i--) {
      String key = ((TextBox) fieldToWidgetMap.get("key" + Integer.toString(i))).getText();
      String value = ((TextBox) fieldToWidgetMap.get("value" + Integer.toString(i))).getText();
      if (key != null && !key.equals("") && value != null && !value.equals("")) {
        kvPairs.put(key, value);
      }
    }
    mapService.saveEvent(who, when, lat, lon, kvPairs, shared, new AsyncCallback<Void>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Failure");
      }

      @Override
      public void onSuccess(Void result) {
        Window.alert("Success");
        Window.Location.assign("/PacoEventServer.html");
        // formPanel.clear();
        // createEventForm();
      }

    });
  }

  private Widget createKeyValueLineButton() {
    Button whatButton = new Button("Add Pair");
    whatButton.addClickListener(new ClickListener() {

      @Override
      public void onClick(Widget sender) {
        whatPanel.add(createWhatFormLine("", ""));
      }

    });
    return whatButton;
  }

  private Widget createWhatFormLine(String key, String value) {
    HorizontalPanel line = new HorizontalPanel();

    VerticalPanel kp = new VerticalPanel();
    line.add(kp);

    Label keyLabel = new Label("Key:");
    kp.add(keyLabel);
    TextBox keyText = new TextBox();
    if (key != null) {
      keyText.setText(key);
    }
    kp.add(keyText);

    VerticalPanel vp = new VerticalPanel();
    line.add(vp);

    Label valueLabel = new Label("Value:");
    vp.add(valueLabel);
    TextBox valueText = new TextBox();
    if (value != null) {
      valueText.setText(value);
    }
    vp.add(valueText);

    String keyValueCountString = Integer.toString(keyValueCounter);
    fieldToWidgetMap.put("key" + keyValueCountString, keyText);
    fieldToWidgetMap.put("value" + keyValueCountString, valueText);
    keyValueCounter++;
    return line;
  }

  private HorizontalPanel createFormLine(String key, String value) {
    HorizontalPanel line = new HorizontalPanel();
    Label keyLabel = new Label(key);
    Label colon = new Label(":");
    TextBox valueBox = new TextBox();
    if (value != null) {
      valueBox.setText(value);
    }
    line.add(keyLabel);
    line.add(colon);
    line.add(valueBox);
    fieldToWidgetMap.put(key, valueBox);
    return line;
  }

  private HorizontalPanel createBooleanFormLine(String key, boolean value) {
    HorizontalPanel line = new HorizontalPanel();
    Label keyLabel = new Label(key);
    Label colon = new Label(":");
    CheckBox valueBox = new CheckBox();
    valueBox.setChecked(value);
    line.add(keyLabel);
    line.add(colon);
    line.add(valueBox);
    fieldToWidgetMap.put(key, valueBox);
    return line;
  }

  private HorizontalPanel createFormLine(String key) {
    return createFormLine(key, null);
  }

}