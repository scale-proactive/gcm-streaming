package org.inria.scale.streams.operators;

import java.util.List;

import org.inria.scale.streams.InStream;
import org.inria.scale.streams.MulticastInStream;
import org.javatuples.Tuple;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;

/**
 * <p>
 * Base abstraction for any kind of component that needs to forward an operation
 * result to the next components in the graph, allowing for more than one output
 * source in the streaming application.
 * </p>
 * <p>
 * This class handles the binding of this client interface in the underlying GCM
 * platform implementing the {@link BindingController}, and also provides an
 * interface for extending classes to forward the processed tuples in a simple
 * way using [@link {@link #send(List)}.
 * 
 * @see MulticastInStream
 * 
 * @author moliva
 *
 */
public class RouterOperator implements BindingController, InStream {

	public static final String CLIENT_INTERFACE_NAME = "out";
	public static final int DEFAULT_INPUT_SOURCE = 0;

	private MulticastInStream out;

	/**
	 * Provided method for sending the tuples forward in the application graph to
	 * the next components.
	 * 
	 * @param tuples
	 *          Tuples generated by the current operation in the order they should
	 *          be sent.
	 */
	@SuppressWarnings("unchecked")
	public void send(final List<? extends Tuple> tuples) {
		out.receive(1, (List<Tuple>) tuples);
	}
	
	@Override
	public void receive(final int inputSource, final List<Tuple> newTuples) {
		send(newTuples);
	}

	// //////////////////////////////////////////////
	// ******* BindingController *******
	// //////////////////////////////////////////////

	@Override
	public String[] listFc() {
		return new String[] { CLIENT_INTERFACE_NAME };
	}

	@Override
	public Object lookupFc(final String clientItfName) throws NoSuchInterfaceException {
		if (clientItfName.equals(CLIENT_INTERFACE_NAME)) {
			return out;
		}

		return null;
	}

	@Override
	public void bindFc(final String clientItfName, final Object serverItf) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if (clientItfName.equals(CLIENT_INTERFACE_NAME)) {
			out = (MulticastInStream) serverItf;
		}
	}

	@Override
	public void unbindFc(final String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if (clientItfName.equals(CLIENT_INTERFACE_NAME)) {
			out = null;
		}
	}

}