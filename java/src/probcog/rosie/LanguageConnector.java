package probcog.rosie;

import sml.Agent;
import sml.Agent.OutputEventInterface;
import sml.Agent.RunEventInterface;
import sml.Identifier;
import sml.WMElement;
import sml.smlRunEventId;

import april.util.TimeUtil;

import probcog.rosie.testing.ActionType;

import probcog.rosie.language.AgentMessageParser;
import probcog.rosie.language.Patterns.LingObject;
import probcog.rosie.world.Messages;
import probcog.rosie.world.WMUtil;

public class LanguageConnector implements OutputEventInterface, RunEventInterface {
	private SoarAgent soarAgent;
	
	private Messages messages;
	
	Identifier languageId = null;
	
	private int totalIndexes = 0;
	private int indexFailures = 0;
	
    public LanguageConnector(SoarAgent soarAgent, String dictionaryFile, String grammarFile)
    {
    	this.soarAgent = soarAgent;
    	
    	messages = new Messages(dictionaryFile, grammarFile);
    	
        String[] outputHandlerStrings = { "send-message", "remove-message", "push-segment", 
        		"pop-segment", "report-interaction", "indexing-report" };

        for (String outputHandlerString : outputHandlerStrings)
        {
        	soarAgent.getAgent().AddOutputHandler(outputHandlerString, this, null);
        }
        
        soarAgent.getAgent().RegisterForRunEvent(
                smlRunEventId.smlEVENT_BEFORE_INPUT_PHASE, this, null);
    }
    
    public void clear(){
    	messages.destroy();
    	clearLGMessages();
    }

    public void clearLGMessages(){
    	soarAgent.commitChanges();
    }
    
    public void destroyMessage(int id){
    	if(messages.getIdNumber() == id){
    		messages.destroy();
    	} 
    }
    
    public void newMessage(String message){
    	messages.addMessage(message);
    }
    
    public void runEventHandler(int eventID, Object data, Agent agent, int phase)
    {
    	long time = 0;
    	if(Rosie.DEBUG_TRACE){
    		time = TimeUtil.utime();
    	}
    	
    	Identifier outputLink = agent.GetOutputLink();
    	if(outputLink != null){
        	WMElement waitingWME = outputLink.FindByAttribute("waiting", 0);
        	ChatFrame.Singleton().setReady(waitingWME != null);
    	}
    	Identifier inputLink = agent.GetInputLink();
    	if(languageId == null){
    		languageId = inputLink.CreateIdWME("language");
    	}
    	if(languageId != null){
    		messages.updateInputLink(languageId);
    	}
    	
    	if(Rosie.DEBUG_TRACE){
			System.out.println(String.format("%-20s : %d", "LANGUAGE CONNECTOR", (TimeUtil.utime() - time)/1000));
    	}
    }

    @Override
    public void outputEventHandler(Object data, String agentName,
            String attributeName, WMElement wme)
    {
    	synchronized(this){
    		if (!(wme.IsJustAdded() && wme.IsIdentifier()))
            {
                return;
            }
    		Identifier id = wme.ConvertToIdentifier();
            System.out.println(wme.GetAttribute());
            

            try{
	            if (wme.GetAttribute().equals("send-message"))
	            {
	                processOutputLinkMessage(id);
	            }
	            else if (wme.GetAttribute().equals("remove-message"))
	            {
	            	processRemoveMesageCommand(id);
	            }
	            else if(wme.GetAttribute().equals("push-segment"))
	            {
	            	processPushSegmentCommand(id);
	            } 
	            else if(wme.GetAttribute().equals("pop-segment"))
	            {
	            	processPopSegmentCommand(id);
	            }
	            else if(wme.GetAttribute().equals("report-interaction")){
	            	processReportInteraction(id);
	            } 
	            else if(wme.GetAttribute().equals("indexing-report")){
	            	processReportIndexing(id);
	            }
	            soarAgent.commitChanges();
            } catch (IllegalStateException e){
            	System.out.println(e.getMessage());
            }
    	}
    }

	private void processOutputLinkMessage(Identifier messageId)
    {	
		if (messageId == null)
        {
            return;
        }

        if (messageId.GetNumberChildren() == 0)
        {
            messageId.CreateStringWME("status", "error");
            throw new IllegalStateException("Message has no children");
        }
        
        if(WMUtil.getIdentifierOfAttribute(messageId, "first") == null){
        	processAgentMessageStructureCommand(messageId);
        } else {
        	processAgentMessageStringCommand(messageId);
        }
    }

	private void processRemoveMesageCommand(Identifier messageId) {
		int id = Integer.parseInt(WMUtil.getValueOfAttribute(messageId, "id", "Error (remove-message): No id"));
		destroyMessage(id);
		messageId.CreateStringWME("status", "complete");
	}
	
    private void processAgentMessageStructureCommand(Identifier messageId)
    {
        String type = WMUtil.getValueOfAttribute(messageId, "type",
                "Message does not have ^type");
        String message = "";
        message = AgentMessageParser.translateAgentMessage(messageId);
        if(!message.equals("")){
            ChatFrame.Singleton().addMessage(message, ActionType.Agent);
        }
        messageId.CreateStringWME("status", "complete");
    }
	
	private void processAgentMessageStringCommand(Identifier messageId){

        String message = "";
        WMElement wordsWME = messageId.FindByAttribute("first", 0);
        if (wordsWME == null || !wordsWME.IsIdentifier())
        {
            messageId.CreateStringWME("status", "error");
            throw new IllegalStateException("Message has no first attribute");
        }
        Identifier currentWordId = wordsWME.ConvertToIdentifier();

        // Follows the linked list down until it can't find the 'rest' attribute
        // of a WME
        while (currentWordId != null)
        {
            Identifier nextWordId = null;
            for (int i = 0; i < currentWordId.GetNumberChildren(); i++)
            {
                WMElement child = currentWordId.GetChild(i);
                if (child.GetAttribute().equals("value"))
                {
                    message += child.GetValueAsString()+ " ";
                }
                else if (child.GetAttribute().equals("next")
                        && child.IsIdentifier())
                {
                    nextWordId = child.ConvertToIdentifier();
                }
            }
            currentWordId = nextWordId;
        }

        if (message == "")
        {
            messageId.CreateStringWME("status", "error");
            throw new IllegalStateException("Message was empty");
        }

        message += ".";
        ChatFrame.Singleton().addMessage(message.substring(0, message.length() - 1), ActionType.Agent);

        messageId.CreateStringWME("status", "complete");
    }
    
    private void processPushSegmentCommand(Identifier id){
    	//String type = WMUtil.getValueOfAttribute(id, "type", "Error (push-segment): No ^type attribute");
    	//String originator = WMUtil.getValueOfAttribute(id, "originator", "Error (push-segment): No ^originator attribute");
    	//Rosie.Singleton().getSoarAgent().getStack().pushSegment(type, originator);
    	id.CreateStringWME("status", "complete");
    }
    
    private void processPopSegmentCommand(Identifier id){
    	//Rosie.Singleton().getSoarAgent().getStack().popSegment();
    	id.CreateStringWME("status", "complete");
    }
    
    
    private void processReportInteraction(Identifier id){
    	String type = WMUtil.getValueOfAttribute(id, "type");
    	//String originator = WMUtil.getValueOfAttribute(id, "originator");
    	//Identifier sat = WMUtil.getIdentifierOfAttribute(id, "satisfaction");
    	//String eventName = sat.GetChild(0).GetAttribute();
    	//WMElement eventTypeWME = sat.GetChild(0).ConvertToIdentifier().FindByAttribute("type", 0);
    	Identifier context = WMUtil.getIdentifierOfAttribute(id, "context");
    	
    	String message = "";
    	if(type.equals("get-next-task")){
    		message = "I am idle and waiting for you to initiate a new interaction";
    	} else if(type.equals("get-next-subaction")){
    		String verb = WMUtil.getValueOfAttribute(context, "verb");  
    		message = "What is the next step in performing '" + verb + "'?";
    	} else if(type.equals("ask-property-name")){
    		String word = WMUtil.getValueOfAttribute(context, "word");
    		message = "I do not know the category of " + word + ". " + 
    		"You can say something like 'a shape' or 'blue is a color'";
    	} else if(type.equals("which-question")){
    		Identifier obj = WMUtil.getIdentifierOfAttribute(context, "object");
    		String objStr = LingObject.createFromSoarSpeak(obj, "outgoing-desc").toString();
    		message = "I see multiple examples of '" + objStr + "' and I need clarification";
    	} else if(type.equals("teaching-request")){
    		Identifier obj = WMUtil.getIdentifierOfAttribute(context, "object");
    		if (obj != null)
    		{	
    			String objStr = LingObject.createFromSoarSpeak(obj, "outgoing-desc").toString();
    			message = "Please give me teaching examples of '" + objStr + "' and tell me 'finished' when you are done.";
    		}
    	} else if(type.equals("get-goal")){
    		String verb = WMUtil.getValueOfAttribute(context, "verb");
    		message = "Please tell me what the goal of '" + verb + "'is.";
    	}
    	
    	if(!message.isEmpty()){
        	ChatFrame.Singleton().addMessage(message, ActionType.Agent);
    	}
        id.CreateStringWME("status", "complete");
    }
    
    private void processReportIndexing(Identifier id){
    	// AM: Enable this code to report the number of successful indexes
//    	String type = WMUtil.getValueOfAttribute(id, "type");
//    	//System.out.println("INDEXTYPE " + type);
//    	if(type.equals("new")){
//    		totalIndexes++;
//    	} else if(type.equals("failure")){
//    		indexFailures++;
//    	} else if(type.equals("report")){
//        	ChatFrame.Singleton().addMessage("Indexing Success: (" + (totalIndexes-indexFailures) + "/" + totalIndexes + ")");
//    	}
    	
        id.CreateStringWME("status", "complete");
    }
}
