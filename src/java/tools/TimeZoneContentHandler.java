/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.Stack;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Imm
 */
public class TimeZoneContentHandler extends DefaultHandler {

        private String timeZoneOffset;
        private String timeZoneName;
        private Stack elementStack = new Stack();

        public String getTimeZoneName() {
            return timeZoneName;
        }

        public String getTimeZoneOffset() {
            return timeZoneOffset;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String value = new String(ch, start, length).trim();
            if (value.length() == 0) {
                return; // ignore white space
            }
            //handle the value based on to which element it belongs
            switch (currentElement()) {
                case "dst_offset":
                    timeZoneOffset = value;
                    break;
                case "time_zone_name":
                    timeZoneName = value;
                    break;
            }
        }

        private String currentElement() {
            return (String) this.elementStack.peek();
        }

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
            this.elementStack.push(qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            //Remove last added  element
            this.elementStack.pop();
        }
    }