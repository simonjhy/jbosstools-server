/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.openshift.express.internal.ui.wizard;

import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.openshift.express.client.IApplication;
import org.jboss.tools.openshift.express.client.utils.RFC822DateUtils;
import org.jboss.tools.openshift.express.internal.ui.OpenshiftImages;

/**
 * @author André Dietisheim
 */
public class ApplicationDetailsDialog extends TitleAreaDialog {

	private IApplication application;

	public ApplicationDetailsDialog(IApplication application, Shell parentShell) {
		super(parentShell);
		this.application = application;
		setTitleImage(OpenshiftImages.OPENSHIFT_LOGO_WHITE_MEDIUM_IMG);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Application Details");
		setTitle(NLS.bind("Application {0}", application.getName()));

		Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(6, 6).applyTo(container);

		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(separator);

		createDetails("Name", application.getName(), container);
		createDetails("Type", application.getCartridge().getName(), container);
		createDetails("Creation Time", new ErrorMessageCallable<String>("Creation Time") {

			@Override
			public String call() throws Exception {
				return RFC822DateUtils.getString(application.getCreationTime());
			}
		}.get(), container);
		createDetails("UUID", new ErrorMessageCallable<String>("UUID") {

			@Override
			public String call() throws Exception {
				return application.getUUID();
			}
		}.get(), container);
		createDetails("Git URL", new ErrorMessageCallable<String>("Git URL") {

			@Override
			public String call() throws Exception {
				return application.getGitUri();
			}
		}.get(), container);
		createDetails("Public URL", new ErrorMessageCallable<String>("Public URL") {

			@Override
			public String call() throws Exception {
				return application.getApplicationUrl();
			}
		}.get(), container);

		return container;
	}

	private void createDetails(String name, String value, Composite container) {
		Label label = new Label(container, SWT.None);
		label.setText(name);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(label);
		Text text = new Text(container, SWT.BORDER);
		text.setEditable(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(text);
		text.setText(value);
	}


	private abstract class ErrorMessageCallable<T> implements Callable<T> {

		private String fieldName;

		public ErrorMessageCallable(String fieldName) {
			this.fieldName = fieldName;
		}

		public T get() {
			try {
				return call();
			} catch (Exception e) {
				setErrorMessage(NLS.bind("Could not get {0}: {1}", fieldName, e.getMessage()));
				return null;
			}
		}

		@Override
		public abstract T call() throws Exception;
	}
}
