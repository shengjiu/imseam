package com.imseam.chatpage.pageflow;

import java.io.Reader;

import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.ProblemListener;
import org.xml.sax.InputSource;

public class ChatflowParser extends JpdlXmlReader {

  private static final long serialVersionUID = 1L;

  public ChatflowParser(InputSource inputSource, ProblemListener problemListener) {
    super(inputSource, problemListener);
  }

  public ChatflowParser(InputSource inputSource) {
    super(inputSource);
  }

  public ChatflowParser(Reader reader) {
    super(reader);
  }
  
  @Override
  public void readNodes(Element nodeCollectionElement, NodeCollection nodeCollection) {
    super.readNodes(nodeCollectionElement, nodeCollection);
    
    if ("chatflow-definition".equals(nodeCollectionElement.getName())) {
      String startPageName = nodeCollectionElement.attributeValue("start-node");
      if (startPageName==null) {
        Element startPageElement = nodeCollectionElement.element("start-node");
        if (startPageElement!=null) {
          startPageName = startPageElement.attributeValue("name");
        }
      }
      if (startPageName!=null) {
        Node startPage = getProcessDefinition().getNode(startPageName);
        if (startPage!=null) {
          getProcessDefinition().setStartState(startPage);
        }
      }
    }
  }
}
