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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.oniguruma.OnigScanner;

/**
 * @see <a href=
 *      "https://github.com/microsoft/vscode-textmate/blob/9157c7f869219dbaf9a5a5607f099c00fe694a29/src/rule.ts#L295">
 *      github.com/Microsoft/vscode-textmate/blob/master/src/rule.ts</a>
 */
final class RegExpSourceList {

	private final List<RegExpSource> items = new ArrayList<>();
	private boolean hasAnchors;

	@Nullable
	private CompiledRule cached;
	private final CompiledRule[][] anchorCache = new CompiledRule[2][2];

	private void disposeCache() {
		cached = null;
		anchorCache[0][0] = null;
		anchorCache[0][1] = null;
		anchorCache[1][0] = null;
		anchorCache[1][1] = null;
	}

	void add(final RegExpSource item) {
		items.add(item);
		if (!hasAnchors) {
			hasAnchors = item.hasAnchor();
		}
	}

	void remove(final RegExpSource item) {
		items.add(0, item);
		if (!hasAnchors) {
			hasAnchors = item.hasAnchor();
		}
	}

	int length() {
		return items.size();
	}

	void setSource(final int index, final String newSource) {
		final RegExpSource r = items.get(index);
		if (!Objects.equals(r.getSource(), newSource)) {
			disposeCache();
			r.setSource(newSource);
		}
	}

	CompiledRule compile() {
		var cached = this.cached;
		if (cached == null) {
			final List<String> regexps = items.stream().map(RegExpSource::getSource).collect(Collectors.toList());
			cached = this.cached = new CompiledRule(new OnigScanner(regexps), getRules());
		}
		return cached;
	}

	CompiledRule compileAG(final boolean allowA, final boolean allowG) {
		if (!hasAnchors) {
			return compile();
		}

		final var indexA = allowA ? 1 : 0;
		final var indexG = allowG ? 1 : 0;

		var rule = anchorCache[indexA][indexG];
		if (rule == null) {
			rule = anchorCache[indexA][indexG] = resolveAnchors(allowA, allowG);
		}
		return rule;
	}

	private CompiledRule resolveAnchors(final boolean allowA, final boolean allowG) {
		final List<String> regexps = items.stream().map(e -> e.resolveAnchors(allowA, allowG))
				.collect(Collectors.toList());
		return new CompiledRule(new OnigScanner(regexps), getRules());
	}

	private int[] getRules() {
		final var ruleIds = new int[items.size()];
		for (int i = 0; i < ruleIds.length; i++) {
			ruleIds[i] = items.get(i).ruleId;
		}
		return ruleIds;
	}
}
