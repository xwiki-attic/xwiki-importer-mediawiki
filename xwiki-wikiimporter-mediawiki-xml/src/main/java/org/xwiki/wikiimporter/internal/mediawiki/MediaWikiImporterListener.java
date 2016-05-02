/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.wikiimporter.internal.mediawiki;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.QuotationBlock;
import org.xwiki.rendering.internal.parser.XDOMGeneratorListener;
import org.xwiki.rendering.listener.reference.DocumentResourceReference;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.wikiimporter.bridge.WikiImporterDocumentBridge;
import org.xwiki.wikiimporter.internal.importer.WikiImporterLogger;
import org.xwiki.wikiimporter.internal.mediawiki.wiki.MediaWikiAttachment;
import org.xwiki.wikiimporter.internal.mediawiki.wiki.MediaWikiPage;
import org.xwiki.wikiimporter.internal.mediawiki.wiki.MediaWikiPageRevision;
import org.xwiki.wikiimporter.listener.AbstractWikiImporterListenerXDOM;

/**
 * Contains callback events called when a document to be imported has been parsed by MediWiki XML Parser
 * 
 * @version $Id$
 */
public class MediaWikiImporterListener extends AbstractWikiImporterListenerXDOM
{
    private MediaWikiPage currentPage;

    private MediaWikiPageRevision currentPageRevision;

    private String currentId;

    private String currentMediaWikiContent;

    private MediaWikiImportParameters importParams;

    private List<String> attachments = new ArrayList<String>();

    private int macroErrors;

    private WikiImporterLogger logger;

    private WikiImporterDocumentBridge docBridge;

    public MediaWikiImporterListener(ComponentManager componentManager, MediaWikiImportParameters params)
        throws ComponentLookupException
    {
        this.logger = componentManager.lookup(WikiImporterLogger.class);
        this.docBridge = componentManager.lookup(WikiImporterDocumentBridge.class);
        this.importParams = params;
        this.currentMediaWikiContent = "";
    }

    private void newXDOMGeneratorListener()
    {
        setWrappedListener(new XDOMGeneratorListener()
        {
            public Stack<Block> getStack()
            {
                try {
                    Field field = getClass().getDeclaredField("stack");
                    field.setAccessible(true);

                    return (Stack<Block>) field.get(this);
                } catch (Exception e) {

                }

                return null;
            }
        });
    }

    private XDOMGeneratorListener getXDOMGeneratorListener()
    {
        return (XDOMGeneratorListener) getWrappedListener();
    }

    private Stack<Block> getStack()
    {
        try {
            Field field = XDOMGeneratorListener.class.getDeclaredField("stack");
            field.setAccessible(true);

            return (Stack<Block>) field.get(getXDOMGeneratorListener());
        } catch (Exception e) {

        }

        return null;
    }

    private List<Block> generateListFromStack()
    {
        try {
            Method method = XDOMGeneratorListener.class.getDeclaredMethod("generateListFromStack");
            method.setAccessible(true);

            return (List<Block>) method.invoke(getXDOMGeneratorListener());
        } catch (Exception e) {

        }

        return null;
    }

    public void setCurrentMediaWikiContent(String currentContent) {
        this.currentMediaWikiContent = currentContent;
    }

    public String getCurrentMediaWikiContent() {
        return this.currentMediaWikiContent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#beginAttachment(java.lang.String)
     */
    public void beginAttachment(String attachmentName)
    {
        System.out.println("Adding attachment " + attachmentName + " to page " + this.currentPage.getName());
        this.currentPage.addAttachment(new MediaWikiAttachment(this.importParams.getAttachmentSrcPath(),
            attachmentName, this.importParams.getAttachmentExcludeDirs(), this.logger));
        endAttachment();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#beginObject(java.lang.String)
     */
    public void beginObject(String objectType)
    {
        // TODO
        System.out.println("beginObject: " + objectType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#beginWikiPage()
     */
    public void beginWikiPage()
    {
        // Begin Page Flags.
        this.currentPage = new MediaWikiPage(this.importParams.getDefaultSpace());
        this.currentPageRevision = new MediaWikiPageRevision();
        this.currentPage.addRevision(this.currentPageRevision);
        this.currentId = null;
        this.logger.nextPage();
        newXDOMGeneratorListener();
        this.attachments.clear();
        this.macroErrors = 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#beginWikiPageRevision()
     */
    public void beginWikiPageRevision()
    {
        if (this.currentPageRevision == null) {
            this.currentPageRevision = new MediaWikiPageRevision(this.currentPage.getLastRevision());
            this.currentPage.addRevision(this.currentPageRevision);
        }
        this.currentPageRevision.setVersion(null);
        newXDOMGeneratorListener();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#endAttachment()
     */
    public void endAttachment()
    {
        // TODO
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#endObject(java.lang.String)
     */
    public void endObject(String objectType)
    {
        // TODO
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#endWikiPage()
     */
    public void endWikiPage()
    {
        // Logging - set original page title for reference.
        this.logger.getPageLog().setLog(this.currentPage.getLastRevision().getTitle());
        if (this.macroErrors > 0) {
            this.logger.warn("Total Macro Errors reported on this page :" + macroErrors, true);
        }

        // attachments
        if (this.currentPage.getAttachments().size() > 0) {
            this.logger.info("Total Attachments encountered :" + attachments.size(), true);
        }

        try {
            // Save the Wiki Page.
            this.docBridge.addWikiPage(this.currentPage, this.importParams);
        } catch (Exception e) {
            this.logger.error("Failed to create the page: " + e.getMessage(), true);
            // Do nothing.
        }

        this.currentPage = null;
        this.currentPageRevision = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#endWikiPageRevision()
     */
    public void endWikiPageRevision()
    {
       try {
        this.currentPageRevision.setOriginalContent(this.currentMediaWikiContent);
        this.currentPageRevision.setContent(getXDOMGeneratorListener().getXDOM());
        System.out.println("Success getting revision data for page " + currentPageRevision.getTitle() + " for version " + currentPageRevision.getVersion());
       } catch(Exception e) {
        e.printStackTrace();
        System.out.println("Failed to get revision data for page " + currentPageRevision.getTitle() + " for version " + currentPageRevision.getVersion());
        List list = this.currentPage.getRevisions();
        if (list.size()>1)
            list.remove(this.currentPage.getLastRevision());
       }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#onAttachmentRevision(java.lang.String,
     *      org.xml.sax.InputSource)
     */
    public void onAttachmentRevision(String attachmentName, InputSource input)
    {
        // TODO
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.wikiimporter.listener.WikiImporterListener#onProperty(java.lang.String, java.lang.String)
     */
    public void onProperty(String property, String value)
    {
        System.out.println("Found property :  " + property + " :" + value);
        if (property.equals(MediaWikiConstants.PAGE_TITLE_TAG)) {
            this.currentPageRevision.setTitle(value);
        } else if (property.equals(MediaWikiConstants.AUTHOR_TAG)) {
            this.currentPageRevision.setAuthor(value);
        } else if (property.equals(MediaWikiConstants.COMMENT_TAG)) {
            this.currentPageRevision.setComment(value);
        } else if (property.equals(MediaWikiConstants.VERSION_TAG)) {
            if (this.currentId==null) {
                this.currentId = value;
            } else if (this.currentPageRevision.getVersion()==null)
               this.currentPageRevision.setVersion(value);
        } else if (property.equals(MediaWikiConstants.IS_MINOR_TAG)) {
            this.currentPageRevision.setMinorEdit(Boolean.valueOf(value));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.listener.WrappingListener#endQuotation(java.util.Map)
     */
    // TODO: this should be fixed in the MediaWiki parser itself
   /*
    public void endQuotation(Map<String, String> parameters)
    {
        QuotationBlock quotationBlock = new QuotationBlock(generateListFromStack(), parameters);
        if (!getStack().isEmpty() && getStack().peek() instanceof QuotationBlock) {
            QuotationBlock lastBlock = (QuotationBlock) getStack().peek();
            lastBlock.addChildren(quotationBlock.getChildren());
        } else {
            getStack().push(quotationBlock);
        }
    }
   */

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.listener.WrappingListener#beginLink(org.xwiki.rendering.listener.reference.ResourceReference,
     *      boolean, java.util.Map)
     */
    public void beginLink(ResourceReference reference, boolean isFreeStandingURI, Map<String, String> parameters)
    {
        String linkReference = reference.getReference();
        System.out.println("beginLink with reference: " + linkReference);
        // Convert Categories to Tags.
        if (linkReference.startsWith("Category")) {
            String[] refData = linkReference.split(":");
            if (refData.length>1)
             this.currentPageRevision.addTag(refData[1]);
            return;
        }

        if (linkReference.contains("::")) {
            // we don't know how to treat these links
            return;
        }

        // Convert from MediaWiki link to XWiki link
        reference = converReference(reference);

        super.beginLink(reference, isFreeStandingURI, parameters);
    }


    public void onImage(ResourceReference reference, boolean isFreeStandingURI, Map<String, String> parameters)
    {
        System.out.println("Converting image reference: " + reference);
        ResourceReference xwikiLink =
            new ResourceReference(reference.getReference(), reference.getType());
        String resourceName = reference.getReference();
        resourceName = resourceName.replaceAll(" ", "_");
        xwikiLink.setReference("image:" + resourceName);
        beginAttachment(resourceName);

        super.onImage(xwikiLink, isFreeStandingURI, parameters);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.listener.WrappingListener#endLink(org.xwiki.rendering.listener.reference.ResourceReference,
     *      boolean, java.util.Map)
     */
    public void endLink(ResourceReference reference, boolean isFreeStandingURI, Map<String, String> parameters)
    {
        String linkReference = reference.getReference();
        System.out.println("endLink with reference: " + linkReference);

        // Convert Categories to Tags.
        if (linkReference.startsWith("Category")||linkReference.contains("::")) {
            return;
        }

        // Convert from MediaWiki link to XWiki link
        reference = converReference(reference);

        super.endLink(reference, isFreeStandingURI, parameters);
    }

    /**
     * Convert from MediaWiki reference to XWiki reference.
     */
    private ResourceReference converReference(ResourceReference mediaWikiReference)
    {
      System.out.println("Converting reference: " + mediaWikiReference);
      try {
        boolean isImage = false;

        // If link reference is a external url
        if (-1 != mediaWikiReference.getReference().indexOf("://")) {
            return mediaWikiReference;
        }

        // if link reference is an email
        if (mediaWikiReference.getReference().startsWith("mailto:")) {
            int spaceOccurence = mediaWikiReference.getReference().indexOf(' ');
            if (-1 != spaceOccurence) {

            }

            return mediaWikiReference;
        }

/*
        if (mediaWikiReference.getReference().startsWith("Image:")) {
            System.out.println("Found Image: in reference");
            isImage = true;
            mediaWikiReference.setReference(mediaWikiReference.getReference().substring(6));
        }
*/

        ResourceReference xwikiLink =
            new ResourceReference(mediaWikiReference.getReference(), mediaWikiReference.getType());

        xwikiLink.setParameters(mediaWikiReference.getParameters());

        // Handle Colon (:) - Links like [[Space:Page]]
        if (xwikiLink.getReference().contains(":") && !xwikiLink.getReference().endsWith(":")) {
            String[] parts = xwikiLink.getReference().split(":");
            String nameSpace = parts[0];
            if (StringUtils.isNotEmpty(this.importParams.getTargetSpace())) {
                nameSpace = this.importParams.getTargetSpace();
            }
            String resourceName = parts[1];
            System.out.println("Checking namespace=" + nameSpace + " resource=" + resourceName);
        
            if (isImage(nameSpace, resourceName)) {
                resourceName.replaceAll(" ", "_");
                System.out.println("Found image " + resourceName);
                xwikiLink.setReference("image:" + resourceName);
                beginAttachment(resourceName);

            } else if (nameSpace.equalsIgnoreCase("media") || nameSpace.equalsIgnoreCase("file")) {
                System.out.println("Found attachment " + resourceName);
                xwikiLink.setReference("attach:" + resourceName);
                beginAttachment(resourceName);

            } else if (-1 != resourceName.indexOf('/')) {
                xwikiLink.setReference(nameSpace + "."
                    + MediaWikiConstants.convertPageName(resourceName.substring(resourceName.lastIndexOf('/') + 1)));
            } else {
                xwikiLink.setReference(nameSpace + "." + MediaWikiConstants.convertPageName(resourceName));
            }
        } else if (StringUtils.isNotEmpty(xwikiLink.getReference())) {
            // If linkreference is not referred to a space, set the default space as Main.
            xwikiLink.setReference(getDefaultSpace() + "."
                + MediaWikiConstants.convertPageName(xwikiLink.getReference()));
        }

        // Fix Category Link [[:Category:Help|HELP]]
        if (xwikiLink.getReference().startsWith(":Category:") || xwikiLink.getReference().startsWith(":category:")) {
            String categoryReference = xwikiLink.getReference().substring(":Category:".length()).trim();
            if (!"".equals(categoryReference)) {
                xwikiLink.setReference("Main.Tags");
                xwikiLink.setParameter(DocumentResourceReference.QUERY_STRING, "do=viewTag&tag=" + categoryReference);
            } else {

            }
        }

        // Handle hierarchy ('/')
        if (-1 != xwikiLink.getReference().indexOf('/')) {
            xwikiLink.setReference(getDefaultSpace()
                + "."
                + MediaWikiConstants.convertPageName(xwikiLink.getReference().substring(
                    xwikiLink.getReference().lastIndexOf('/') + 1)));
        }

        System.out.println("Found link " + xwikiLink.getReference());

        return xwikiLink;
       } catch (Exception e) {
        System.out.println("Failed to convert reference");
        e.printStackTrace();
        return mediaWikiReference;
       }
    }

    /**
     * Check if the file is a image.
     * 
     * @param nameSpace Namespace of the file.Images usually have File or Image and namespace.
     * @param fileName name of the file with extension
     * @return
     */
    private boolean isImage(String nameSpace, String fileName)
    {
        if (nameSpace.equalsIgnoreCase("image"))
            return true;

        int dotIndex = fileName.indexOf('.');
        String[] fileExtensions = {"png", "gif", "jpg", "jpeg", "svg", "tiff", "tif"};
        if ((nameSpace.equalsIgnoreCase("image") || nameSpace.equalsIgnoreCase("file")) && -1 != dotIndex) {
            String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            return Arrays.asList(fileExtensions).contains(fileExtension);
        }

        return false;
    }

    /**
     * @return the default space.
     */
    private String getDefaultSpace()
    {
        if (StringUtils.isNotBlank(this.importParams.getTargetSpace())) {
            return this.importParams.getTargetSpace();
        } else if (StringUtils.isNotBlank(this.importParams.getDefaultSpace())) {
            return this.importParams.getDefaultSpace();
        }

        return "Main";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.listener.Listener#onMacro(java.lang.String, java.util.Map, java.lang.String, boolean)
     */
    public void onMacro(String id, Map<String, String> macroParameters, String content, boolean isInline)
    {
        System.out.println("Found macro " + id);
        if (id.equals("toc") || id.equals("forcetoc")) {
            macroParameters =
                macroParameters != null ? new HashMap<String, String>(macroParameters) : new HashMap<String, String>();
            macroParameters.put("numbered", "true");
        } else {
            id = "warning";
            this.macroErrors++;
        }

        super.onMacro(id, macroParameters, content, isInline);
    }
}
