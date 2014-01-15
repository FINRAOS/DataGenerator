/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.finra.scxmlexec;

import java.net.URL;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 *
 * @author mosama
 */
public class MicrowaveTest {

	public static class MyListener implements SCXMLListener {

		@Override
		public void onEntry(TransitionTarget tt) {
			System.out.println("ONENTRY: Transition target: " + tt.toString());
		}

		@Override
		public void onExit(TransitionTarget tt) {
			System.out.println("ONEXIT: Transition target: " + tt.toString());
		}

		@Override
		public void onTransition(TransitionTarget tt, TransitionTarget tt1, Transition trnstn) {
			System.out.println("ONTRANSITION: Transition target from: " + tt.toString() + " to:" + tt1.toString() + " using transition:" + trnstn.toString());
		}

	}

	public static void main(String args[]) throws Exception {
		// Load the state machine
		SCXML stateMachine = SCXMLParser.parse(new URL("file:///home/mosama/projects/microwave.scxml"), null);
		SCXMLExecutor executor = new SCXMLExecutor();
		JexlEvaluator evaluator = new JexlEvaluator();
		JexlContext context = new JexlContext();
		MyListener listener = new MyListener();

		executor.setRootContext(context);
		executor.setStateMachine(stateMachine);
		executor.addListener(stateMachine, listener);
		executor.setEvaluator(evaluator);

		executor.go();
		executor.triggerEvent(new TriggerEvent("turn_on", TriggerEvent.SIGNAL_EVENT));
	}
}
