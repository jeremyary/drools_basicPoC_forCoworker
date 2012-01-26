package com.cassidian.drools;

import com.cassidian.drools.model.Data;
import com.cassidian.drools.model.Message;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.xml.XmlDumper;
import org.drools.event.rule.*;
import org.drools.io.impl.ClassPathResource;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

/**
 * @author jary
 */
public class MessageRulesTest {
    
    private HashMap<String, LinkedList<ActivationEvent>> eventList;
    private HashMap<String, LinkedList<WorkingMemoryEvent>> factList;

    /***
     * No assertions, just simply flexing the rules to give you an idea of what it looks like
     * when we throw different Message types at it in order to trigger each one of our rules.
     *
     * This is a no-nonsense way to run rules...build the knowledgeBase, build the session, insert
     * our facts, make our decisions, and scrap the session. Retracting the message when a rule is fired
     * isn't necessary when running this way since the session is disposed within scope, but note that
     * it's included in out rules for test demonstration of the knowledge agent (see below).
     * 
     */
    @Test
    public void demonstrateRules() {

        KnowledgeBase knowledgeBase = createKnowledgeBase("messageRules.drl");
        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        // if you want to see what's going on with the agenda behind the scenes prior to "pulling the trigger"
        // and firing all rules, uncomment this line and the built in debug listener will println all agenda events.
        //
        //session.addEventListener(new DebugAgendaEventListener());

        try {
        
            // voice, ALI, data
            Data data = new Data("voice",  true, true);
            Message message = new Message(data);
            session.insert(message);
            session.fireAllRules();
            System.out.println("----------------------------");
    
            // voice, ALI, no data
            data = new Data("voice", true, false);
            message = new Message(data);
            session.insert(message);
            session.fireAllRules();
            System.out.println("----------------------------");
    
            //voice, no ALI, data
            data = new Data("voice", false, true);
            message = new Message(data);
            session.insert(message);
            session.fireAllRules();
            System.out.println("----------------------------");
    
            //voice, no ALI, no data
            data = new Data("voice", false, false);
            message = new Message(data);
            session.insert(message);
            session.fireAllRules();

        } catch(Exception ex) {
            System.out.println(ex);
        } finally {
            session.dispose();
        }
    }

    /***
     * For dynamic rules, you can attach a knowledge agent in order gain the ability to listen
     * to rules changes on the fly. This presents an alternative approach to building up the
     * knowledgeBase and session each time you want to make a decision. Using this method, you
     * could leave the session intact, update your rules, insert your Message into the session
     * and see results. Note that we retract the message in each rule consequence in order to
     * play nicely with this approach.
     *
     * If you want to look into how to accomplish this, the drools team has a much better test case
     * set than I could provide you with short notice, so I'll refer you to their tests. Start
     * by taking a look at KnowledgeAgentTest.java
     *
     * https://github.com/droolsjbpm/drools/tree/master/drools-compiler/src/test/java/org/drools/agent
     *
     * I have another PoC project near completion from some time ago that uses an agent, so I'll
     * see if I can finish that up soon as I have time and send you a link as well.
     */


    /***
     * Taking a look at how to attach our own custom listener to the agenda so that we
     * can test rules by monitoring what's added/removed from the agenda.
     */
    @Test
    public void demonstrateTestingRulesWithAssertions() {

        KnowledgeBase knowledgeBase = createKnowledgeBase("messageRules.drl");
        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        session.addEventListener(getCustomAgendaEventListener());

        try {

            // voice, ALI, data
            Data data = new Data("voice",  true, true);
            Message message = new Message(data);

            session.insert(message);
            assertTrue(eventList.get("added").size() == 1);
            assertTrue(eventList.get("added").get(0) instanceof ActivationCreatedEvent);
            assertTrue(eventList.get("added").get(0).getActivation().getRule().getName().equals("Voice, ALI, Data present"));
            
            session.fireAllRules();
            assertTrue(eventList.get("fired").size() == 1);
            assertTrue(eventList.get("fired").get(0) instanceof AfterActivationFiredEvent);

            initEventList();
            data = new Data("voice", true, false);
            message = new Message(data);

            session.insert(message);
            assertTrue(eventList.get("added").size() == 1);
            assertTrue(eventList.get("added").get(0) instanceof ActivationCreatedEvent);
            assertTrue(eventList.get("added").get(0).getActivation().getRule().getName().equals("Voice, ALI, no Data present"));

            session.fireAllRules();
            assertTrue(eventList.get("fired").size() == 1);
            assertTrue(eventList.get("fired").get(0) instanceof AfterActivationFiredEvent);

            initEventList();
            data = new Data("voice", false, true);
            message = new Message(data);

            session.insert(message);
            assertTrue(eventList.get("added").size() == 1);
            assertTrue(eventList.get("added").get(0) instanceof ActivationCreatedEvent);
            assertTrue(eventList.get("added").get(0).getActivation().getRule().getName().equals("Voice, no ALI, withData"));

            session.fireAllRules();
            assertTrue(eventList.get("fired").size() == 1);
            assertTrue(eventList.get("fired").get(0) instanceof AfterActivationFiredEvent);

            initEventList();
            data = new Data("voice", false, false);
            message = new Message(data);

            session.insert(message);
            assertTrue(eventList.get("added").size() == 1);
            assertTrue(eventList.get("added").get(0) instanceof ActivationCreatedEvent);
            assertTrue(eventList.get("added").get(0).getActivation().getRule().getName().equals("Voice, no ALI, no data"));

            session.fireAllRules();
            assertTrue(eventList.get("fired").size() == 1);
            assertTrue(eventList.get("fired").get(0) instanceof AfterActivationFiredEvent);

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            session.dispose();
        }
    }

    /***
     * We'll also take a look at adding a listener to the working memory just for
     * completeness which allows us to see when facts are added/removed from the session.
     */
    @Test
    public void demonstrateAssertionsAgainstMemory() {

        KnowledgeBase knowledgeBase = createKnowledgeBase("messageRules.drl");
        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();
        
        session.addEventListener(getCustomWorkingMemoryEventListener());
        
        // note that this is also a valid option here if you're just looking for simple output
        //session.addEventListener(new DebugWorkingMemoryEventListener());
        
        try {
            Message message = new Message(new Data("voice", true, true));
            FactHandle messageHandle = session.insert(message);
            assertTrue(eventList.get("inserted").size() == 1);
            assertTrue(eventList.get("inserted").get(0) instanceof ObjectInsertedEvent);
            
            session.update(messageHandle, message);
            assertTrue(eventList.get("updated").size() == 1);
            assertTrue(eventList.get("updated").get(0) instanceof ObjectUpdatedEvent);
            
            session.retract(messageHandle);
            assertTrue(eventList.get("retracted").size() == 1);
            assertTrue(eventList.get("retracted").get(0) instanceof ObjectRetractedEvent);
            
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            session.dispose();
        }
    }

    private void initEventList() {

        eventList = new HashMap<String, LinkedList<ActivationEvent>>();
        eventList.put("added", new LinkedList<ActivationEvent>());
        eventList.put("fired", new LinkedList<ActivationEvent>());
    }
    
    private AgendaEventListener getCustomAgendaEventListener() {

        initEventList();
        
        AgendaEventListener listener = new AgendaEventListener() {

            @Override
            public void activationCreated(ActivationCreatedEvent event) {
                eventList.get("added").push(event);
            }

            @Override
            public void activationCancelled(ActivationCancelledEvent event) {
            }

            @Override
            public void beforeActivationFired(BeforeActivationFiredEvent event) {
            }

            @Override
            public void afterActivationFired(AfterActivationFiredEvent event) {
                eventList.get("fired").push(event);
            }

            @Override
            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
            }

            @Override
            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
            }
        };

        return listener;
    }

    private void initFactList() {

        factList = new HashMap<String, LinkedList<WorkingMemoryEvent>>();
        factList.put("inserted", new LinkedList<WorkingMemoryEvent>());
        factList.put("updated", new LinkedList<WorkingMemoryEvent>());
        factList.put("retracted", new LinkedList<WorkingMemoryEvent>());
    }

    private WorkingMemoryEventListener getCustomWorkingMemoryEventListener() {

        initFactList();

        WorkingMemoryEventListener listener = new WorkingMemoryEventListener() {

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                factList.get("inserted").push(event);
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                factList.get("updated").push(event);
            }

            @Override
            public void objectRetracted(ObjectRetractedEvent event) {
                factList.get("retracted").push(event);
            }
        };

        return listener;
    }

    /***
     * Showing how to convert DRL to XML...I cheated, I prefer to write DRL, so I didn't hand-code the XML :)
     */
    @Test
    public void produceXmlFromDRL() {

        try {
            PackageDescr pkg = new DrlParser().parse(new InputStreamReader(
                    MessageRulesTest.class.getResourceAsStream("messageRules.drl")
            ));
            System.out.println(new XmlDumper().dump(pkg));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private KnowledgeBase createKnowledgeBase(String drlFileName) {
        
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        // if you wanted to consume the XML format of rules, this is where the magic happens
        builder.add(new ClassPathResource(drlFileName, getClass()), ResourceType.DRL);
        if (builder.hasErrors()) {
            throw new RuntimeException(builder.getErrors().toString());
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());
        return knowledgeBase;
    }
}
