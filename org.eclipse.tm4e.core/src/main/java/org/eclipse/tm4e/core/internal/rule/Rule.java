/**
 * Copyright (c) 2015-2017 Angelo ZERR.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Initial code from https://github.com/Microsoft/vscode-textmate/
 * Initial copyright Copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 *
 * Contributors:
 * - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 * - Angelo Zerr <angelo.zerr@gmail.com> - translation and adaptation to Java
 */
package org.eclipse.tm4e.core.internal.rule;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.oniguruma.OnigCaptureIndex;
import org.eclipse.tm4e.core.internal.utils.RegexSource;

/**
 * @see <a href=
 *      "https://github.com/microsoft/vscode-textmate/blob/9157c7f869219dbaf9a5a5607f099c00fe694a29/src/rule.ts#L43">
 *      github.com/Microsoft/vscode-textmate/blob/master/src/rule.ts</a>
 */
public abstract class Rule {

	final RuleId id;

	@Nullable
	private final String name;
	private final boolean nameIsCapturing;

	@Nullable
	private final String contentName;
	private final boolean contentNameIsCapturing;

	Rule(final RuleId id, @Nullable final String name, final @Nullable String contentName) {
		this.id = id;
		this.name = name;
		this.nameIsCapturing = RegexSource.hasCaptures(name);
		this.contentName = contentName;
		this.contentNameIsCapturing = RegexSource.hasCaptures(contentName);
	}

	@Nullable
	public String getName(@Nullable final String lineText, final OnigCaptureIndex @Nullable [] captureIndices) {
		final var name = this.name;
		if (!nameIsCapturing || name == null || lineText == null || captureIndices == null) {
			return name;
		}
		return RegexSource.replaceCaptures(name, lineText, captureIndices);
	}

	@Nullable
	public String getContentName(final String lineText, final OnigCaptureIndex[] captureIndices) {
		final var contentName = this.contentName;
		if (!contentNameIsCapturing || contentName == null) {
			return contentName;
		}
		return RegexSource.replaceCaptures(contentName, lineText, captureIndices);
	}

	public abstract void collectPatternsRecursive(IRuleRegistry grammar, RegExpSourceList out, boolean isFirst);

	public abstract CompiledRule compile(IRuleRegistry grammar, @Nullable String endRegexSource);

	public abstract CompiledRule compileAG(IRuleRegistry grammar, @Nullable String endRegexSource, boolean allowA,
			boolean allowG);

}