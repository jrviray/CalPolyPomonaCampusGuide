package com.a480.cs.cpp.calpolypomonacampusguide;

import android.support.annotation.NonNull;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy03 on 4/29/2017.
 */

public class DataProcessor {

    public List parse(InputStream inputStream) throws XmlPullParserException,IOException{
        try{

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream,null);
            parser.nextTag();
            return readEntryList(parser);
        }
        finally {
            inputStream.close();
        }
    }

    private List readEntryList(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        List entries = new ArrayList();
        while(parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType()!=XmlPullParser.START_TAG)
            {
                continue;
            }
            String name = parser.getName();
            if(name.equals("entry"))
            {
                entries.add(readEntry(parser));
            }
            else
                throw new IOException("need skip");
        }
        return entries;
    }

    private DataEntry readEntry(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        String title = null;
        String description = null;
        String imageName = null;
        String longitude = null;
        String latitude = null;
        while(parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType()!=XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if(name.equals("title"))
            {
                title=readText(parser);
            }
            else if(name.equals("description"))
            {
                description =readText(parser);
            }
            else if(name.equals("image"))
            {
                imageName = readText(parser);
            }
            else if(name.equals("longitude"))
            {
                longitude = readText(parser);
            }
            else if(name.equals("latitude"))
            {
                latitude = readText(parser);
            }
        }
        return new DataEntry(title,description,imageName,Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
