/**
 * Copyright (c) 2015-2017 Angelo ZERR.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.tm4e.languageconfiguration.internal.model;

import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Describes a rule to be evaluated when pressing Enter.
 *
 * @see <a href=
 *      "https://github.com/microsoft/vscode/blob/8e2ec5a7ee1ae5500c645c05145359f2a814611c/src/vs/editor/common/languages/languageConfiguration.ts#L157">
 *      github.com/microsoft/vscode/blob/main/src/vs/editor/common/languages/languageConfiguration.ts#L157</a>
 */
public final class OnEnterRule {

	/**
	 * This rule will only execute if the text before the cursor matches this regular expression.
	 */
	public final Pattern beforeText;

	/**
	 * This rule will only execute if the text after the cursor matches this regular expression.
	 */
	@Nullable
	public final Pattern afterText;

	// TODO @Nullable public final Pattern previousLineText;

	/**
	 * The action to execute.
	 */
	public final EnterAction action;

	public OnEnterRule(final Pattern beforeText, @Nullable final Pattern afterText, final EnterAction action) {
		this.beforeText = beforeText;
		this.afterText = afterText;
		this.action = action;
	}

	/**
	 * Only for unit tests
	 *
	 * @throws PatternSyntaxException if beforeText or afterText contain invalid regex pattern
	 */
	OnEnterRule(final String beforeText, @Nullable final String afterText, final EnterAction action) {
		this.beforeText = Pattern.compile(beforeText);
		this.afterText = afterText == null ? null : Pattern.compile(afterText);
		this.action = action;
	}
}
