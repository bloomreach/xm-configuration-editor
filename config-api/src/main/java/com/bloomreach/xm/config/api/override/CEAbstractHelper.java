/*
 * Copyright 2014-2020 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bloomreach.xm.config.api.override;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.hippoecm.hst.configuration.HstNodeTypes;
import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.AbstractHelper;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CEAbstractHelper extends AbstractHelper {

    private static final Logger log = LoggerFactory.getLogger(CEAbstractHelper.class);

    protected void publishNodeList(final List<Node> lockedNodes) throws RepositoryException {
        String liveConfigurationPath = pageComposerContextService.getEditingLiveConfigurationPath();
        String previewConfigurationPath = pageComposerContextService.getEditingPreviewConfigurationPath();
        final Session session = pageComposerContextService.getRequestContext().getSession();
        final Map<Node, Node> checkReorderMap = new IdentityHashMap<>();
        for (Node lockedNode : lockedNodes) {
            String relPath = lockedNode.getPath().substring(previewConfigurationPath.length());

            if (session.nodeExists(liveConfigurationPath + relPath)) {
                session.removeItem(liveConfigurationPath + relPath);
            }
            if (lockedNode.hasProperty(HstNodeTypes.EDITABLE_PROPERTY_STATE) &&
                    "deleted".equals(lockedNode.getProperty(HstNodeTypes.EDITABLE_PROPERTY_STATE).getString())) {
                lockedNode.remove();
            } else {
                lockHelper.unlock(lockedNode);
                // we can only publish *IF* and only *IF* the parent exists. Otherwise we log an error and continue
//                String liveParentRelPath = StringUtils.substringBeforeLast(relPath, "/");
//                if (!session.nodeExists(liveConfigurationPath + liveParentRelPath)) {
//                    log.warn("Cannot publish preview node '{}' because the live parent '{}' is missing. Skip publishing node",
//                            lockedNode.getPath(), liveConfigurationPath + liveParentRelPath);
//                } else {
                log.info("Publishing '{}'", lockedNode.getPath());
                Node copy = JcrUtils.copy(session, lockedNode.getPath(), liveConfigurationPath + relPath);
                checkReorderMap.put(lockedNode, copy);
//                }
            }
        }
        for (Map.Entry<Node, Node> entry : checkReorderMap.entrySet()) {
            reorderCopyIfNeeded(entry.getKey(), entry.getValue());
        }
    }


}
