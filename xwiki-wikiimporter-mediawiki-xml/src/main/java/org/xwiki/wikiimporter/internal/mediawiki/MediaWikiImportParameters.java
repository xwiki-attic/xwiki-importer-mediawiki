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

import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;
import org.xwiki.properties.annotation.PropertyName;
import org.xwiki.wikiimporter.importer.WikiImportParameters;

/**
 * Parameters for the MediaWiki Importer
 * 
 * @version $Id$
 */
public class MediaWikiImportParameters extends WikiImportParameters
{
    private String srcPath;

    private String attachmentSrcPath;

    private String attachmentExcludeDirs;

    private String defaultSpace;

    private String preserveHistory;

    private String wiki;

    private String allowedImageExtensions;

    /**
     * @param srcPath absolute path of the exported xml file.
     */
    @PropertyName("Source Path")
    @PropertyDescription("Absolute Path to Exported XML file")
    @PropertyMandatory
    public void setSrcPath(String srcPath)
    {
        this.srcPath = srcPath;
    }

    /**
     * @param attachmentSrcPath absolute path to MediaWiki attachments directory.
     */
    @PropertyName("Attachment Path")
    @PropertyDescription("Absolute Path to MediaWiki Attachments Directory")
    public void setAttachmentSrcPath(String attachmentSrcPath)
    {
        this.attachmentSrcPath = attachmentSrcPath;
    }

    /**
     * @param attachmentExcludeDirs the list of directories to be excluded to search for attachments in the MediaWiki
     *            Image directory.
     */
    @PropertyName("Exclude Directories")
    @PropertyDescription("Comma seperated list of all directories to be excluded to search for attachments")
    public void setAttachmentExcludeDirs(String attachmentExcludeDirs)
    {
        this.attachmentExcludeDirs = attachmentExcludeDirs;
    }

    /**
     * @return the srcPath
     */
    public String getSrcPath()
    {
        return srcPath;
    }

    /**
     * @return the attachmentSrcPath
     */
    public String getAttachmentSrcPath()
    {
        return attachmentSrcPath;
    }

    /**
     * @return the attachmentExcludeDirs
     */
    public String getAttachmentExcludeDirs()
    {
        return attachmentExcludeDirs;
    }

    /**
     * @return the defaultSpace
     */
    public String getDefaultSpace()
    {
        return defaultSpace;
    }

    /**
     * @param defaultSpace the defaultSpace to set
     */
    @PropertyName("Default Space")
    @PropertyDescription("Default Space for importing the data.XWiki uses Main as default space")
    public void setDefaultSpace(String defaultSpace)
    {
        this.defaultSpace = defaultSpace;
    }

    /**
     * @return the preserveHistory
     */
    public String getPreserveHistory()
    {
        return preserveHistory;
    }

    /**
     * @param preserveHistory the preserveHistory to set
     */
    @PropertyName("Preserve history")
    @PropertyDescription("Select true to preserve history")
    public void setPreserveHistory(String preserveHistory)
    {
        this.preserveHistory = preserveHistory;
    }

    /**
     * @return the wiki reference
     */
    public String getWiki()
    {
        return wiki;
    }

    /**
     * @param wiki the Wiki Reference into which files will be imported
     */
    @PropertyName("Wiki")
    @PropertyDescription("Wiki Reference into which files will be imported")
    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    /**
     * @return the allowedImageExtensions
     */
    public String getAllowedImageExtensions()
    {
        return allowedImageExtensions;
    }

    /**
     * @param allowedImageExtensions the list of all the image formats to be considered during import
     */
    @PropertyName("Image Extensions")
    @PropertyDescription("Comma seperated list of all the image formats to be considered during import")
    public void setAllowedImageExtensions(String allowedImageExtensions)
    {
        this.allowedImageExtensions = allowedImageExtensions;
    }

}
