package org.jbpm.ui.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

public class DesignerContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	public DesignerContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}

	public void buildContextMenu(IMenuManager manager) {
		GEFActionConstants.addStandardActionGroups(manager);
		IAction action;
 		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

//		action = getActionRegistry().getAction(IWorkbenchActionConstants.PASTE);
//		if (action.isEnabled())
//			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry()
				.getAction(ActionFactory.DELETE.getId());
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

//		action = getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT);
//		if (action.isEnabled())
//			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ActionFactory.SAVE.getId());
		manager.appendToGroup(GEFActionConstants.GROUP_SAVE, action);

	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
