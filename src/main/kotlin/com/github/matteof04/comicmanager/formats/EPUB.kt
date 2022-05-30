/*
 * Copyright (C) 2022 Matteo Franceschini <matteof5730@gmail.com>
 *
 * This file is part of ComicManager.
 * ComicManager is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ComicManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with ComicManager.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.matteof04.comicmanager.formats

import com.sksamuel.scrimage.ImmutableImage
import net.lingala.zip4j.ZipFile
import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.base.Format
import com.github.matteof04.comicmanager.formats.util.*
import com.github.matteof04.comicmanager.util.Panel
import com.github.matteof04.comicmanager.util.PanelMetadata
import com.github.matteof04.comicmanager.util.mkdir
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.copyTo
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

/**
 * The class for the EPUB 3 format
 * This class is used as a base for the [KEPUB] format
 * @property temp [Path] the temporary directory where the class operates
 */
open class EPUB(
    override val path: Path,
    override val bookTitle: String,
    override val author: String,
    override val pageProgressionDirection: PageProgressionDirections,
    override val deviceInformation: DeviceInformation
) : Format {
    protected val chapters: ArrayList<Chapter> = ArrayList()
    protected val temp = createTempDirectory("EPUB_")
    protected val oebps: Path = temp.resolve("OEBPS")
    protected val images: Path = oebps.resolve("Images")
    protected val text: Path = oebps.resolve("Text")
    protected val uuid: UUID = UUID.randomUUID()

    /**
     * Create all the necessary temporary directory
     */
    init {
        oebps.mkdir()
        images.mkdir()
        text.mkdir()
        temp.resolve("META-INF").mkdir()
    }
    override fun addChapter(chapter: Chapter) {
        chapters.add(chapter)
        val imageDir = images.resolve(chapter.title)
        val textDir = text.resolve(chapter.title)
        imageDir.mkdir()
        textDir.mkdir()
        chapter.pages.forEach {
            it.image.copyTo(imageDir.resolve(it.image.name))
            textDir.resolve(it.image.toFile().nameWithoutExtension+".xhtml").toFile().writeText(buildHTML(chapter.title, it.image.name, ImmutableImage.loader().fromPath(it.image).width, ImmutableImage.loader().fromPath(it.image).height))
        }
    }

    override fun build(coverImage: Panel) {
        chapters.forEach { chapter ->
            chapter.pages.sortBy {
                it.image
            }
        }
        chapters.sortBy {
            it.title
        }
        coverImage.image.copyTo(images.resolve("cover.jpg"))
        text.resolve("style.css").toFile().writeText(buildCSS())
        oebps.resolve("toc.ncx").toFile().writeText(buildTOC())
        oebps.resolve("nav.xhtml").toFile().writeText(buildNAV())
        oebps.resolve("content.opf").toFile().writeText(buildOPF())
        temp.resolve("META-INF/container.xml").toFile().writeText(buildXML())
        temp.resolve("mimetype").toFile().writeText("application/epub+zip")
        val zipFile = ZipFile(path.toFile())
        zipFile.addFile(temp.resolve("mimetype").toFile())
        zipFile.addFolder(oebps.toFile())
        zipFile.addFolder(temp.resolve("META-INF").toFile())
        temp.toFile().deleteRecursively()
    }

    /**
     * Build the default CSS for every page
     * @return [String] a formatted string with the default CSS
     */
    protected open fun buildCSS() = """
        @page {
        margin: 0;
        }
        body {
        display: block;
        margin: 0;
        padding: 0;
        }
    """.trimIndent()

    /**
     * Build the HTML file for every page in every chapter
     * @return [String] A formatted string containing the HTML file content
     */
    protected open fun buildHTML(chapterTitle: String, pageName: String, width: Int, height: Int) : String{
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops">
                <head>
                <title>${chapterTitle}_$pageName</title>
                <link href="../style.css" type="text/css" rel="stylesheet"/>
                <meta name="viewport" content="width=$width, height=$height"/>
                </head>
                <body style="">
                <div style="text-align:center;">
                <img width="$width" height="$height" src="../../Images/$chapterTitle/$pageName"/>
                </div>
                </body>
                </html>
            """.trimIndent()
    }

    /**
     * Build the table of content with all the chapters added until now
     * @return [String] A formatted string containing the TOC file content
     */
    protected open fun buildTOC() : String{
        val startString = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ncx version="2005-1" xml:lang="en-US" xmlns="http://www.daisy.org/z3986/2005/ncx/">
            <head>
            <meta name="dtb:uid" content="$uuid"/>
            <meta name="dtb:depth" content="1"/>
            <meta name="dtb:totalPageCount" content="0"/>
            <meta name="dtb:maxPageNumber" content="0"/>
            <meta name="generated" content="true"/>
            </head>
            <docTitle><text>$bookTitle</text></docTitle>
            <navMap>
        """.trimIndent()
        val stopString = """
            </navMap>
            </ncx>
        """.trimIndent()
        var navMap = ""
        chapters.forEach {
            navMap += "<navPoint id=\"Text_${it.title}\"><navLabel><text>${it.title}</text></navLabel><content src=\"Text/${it.title}/${it.pages.first().image.toFile().nameWithoutExtension+".xhtml"}\"/></navPoint>\n"
        }
        return startString + navMap + stopString
    }

    /**
     * Build the navigation file content with all the chapters added until now
     * @return [String] A formatted string containing the NAV file content
     */
    protected open fun buildNAV() : String{
        val startString = """
            <?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops">
            <head>
            <title>$bookTitle</title>
            <meta charset="utf-8"/>
            </head>
            <body>
            <nav xmlns:epub="http://www.idpf.org/2007/ops" epub:type="toc" id="toc">
            <ol>
        """.trimIndent()
        val middleString = """
            </ol>
            </nav>
            <nav epub:type="page-list">
            <ol>
        """.trimIndent()
        val stopString = """
            </ol>
            </nav>
            </body>
            </html>
        """.trimIndent()
        var navMap = ""
        chapters.forEach {
            navMap += "<li><a href=\"Text/${it.title}/${it.pages.first().image.toFile().nameWithoutExtension+".xhtml"}\">${it.title}</a></li>"
        }
        return startString + navMap + middleString + navMap + stopString
    }

    /**
     * Build the OPF file content with all the chapters added until now
     * @return [String] A formatted string containing the OPF file content
     */
    protected open fun buildOPF() : String{
        val startPackage = """
            <?xml version="1.0" encoding="UTF-8"?>
            <package version="3.0" unique-identifier="BookID" xmlns="http://www.idpf.org/2007/opf">
        """.trimIndent()
        val metadata = """
            <metadata xmlns:opf="http://www.idpf.org/2007/opf" xmlns:dc="http://purl.org/dc/elements/1.1/">
            <dc:title>$bookTitle</dc:title>
            <dc:language>en-US</dc:language>
            <dc:identifier id="BookID">$uuid</dc:identifier>
            <dc:contributor id="contributor">ComicManager</dc:contributor>
            <dc:creator>$author</dc:creator>
            <meta property="dcterms:modified">${LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))}</meta>
            <meta name="cover" content="cover"/>
            <meta property="rendition:spread">landscape</meta>
            <meta property="rendition:layout">pre-paginated</meta>
            </metadata>
        """.trimIndent()
        val startManifest = """
            <manifest>
            <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
            <item id="nav" href="nav.xhtml" properties="nav" media-type="application/xhtml+xml"/>
            <item id="cover" href="Images/cover.jpg" media-type="image/jpeg" properties="cover-image"/>
            <item id="css" href="Text/style.css" media-type="text/css"/>
        """.trimIndent()
        val stopManifest = "</manifest>\n"
        val startSpine = "<spine page-progression-direction=\"$pageProgressionDirection\" toc=\"ncx\">\n"
        val stopSpine = "</spine>\n"
        val stopPackage = "</package>\n"
        var manifest = ""
        chapters.forEach { chapter ->
            chapter.pages.forEach { page ->
                manifest += """
                    <item id="page_Images_${chapter.title}_${page.image.toFile().nameWithoutExtension}" href="Text/${chapter.title}/${page.image.toFile().nameWithoutExtension+".xhtml"}" media-type="application/xhtml+xml"/>
                    <item id="img_Images_${chapter.title}_${page.image.toFile().nameWithoutExtension}" href="Images/${chapter.title}/${page.image.name}" media-type="image/jpeg"/>
                """.trimIndent()
            }
        }
        var spine = ""
        chapters.forEach { chapter ->
            var pageside = when(pageProgressionDirection){
                PageProgressionDirections.RIGHT_TO_LEFT -> PageSide.RIGHT
                PageProgressionDirections.LEFT_TO_RIGHT -> PageSide.LEFT
            }
            val paginatedPanels = ArrayList<PaginatedPanel>()
            chapter.pages.forEach {
                pageside = if (it.metadata != null){
                    when(it.metadata){
                        PanelMetadata.ROTATED -> paginatedPanels.add(PaginatedPanel(it, PageSide.CENTER, DeviceOrientation.PORTRAIT))
                        PanelMetadata.FIRST_SPLIT -> paginatedPanels.add(PaginatedPanel(it, pageProgressionDirection.getFirstSide(), DeviceOrientation.LANDSCAPE))
                        PanelMetadata.SECOND_SPLIT -> paginatedPanels.add(PaginatedPanel(it, pageProgressionDirection.getFirstSide().opposite(), DeviceOrientation.LANDSCAPE))
                    }
                    pageProgressionDirection.getFirstSide()
                }else{
                    paginatedPanels.add(PaginatedPanel(it, pageside))
                    pageside.opposite()
                }
            }
            paginatedPanels.forEachIndexed { index, paginatedPanel ->
                if (index-1 >= 0){
                    if (paginatedPanel.pageSide == PageSide.CENTER){
                        if (paginatedPanels[index-1].pageSide == pageProgressionDirection.getFirstSide()){
                            paginatedPanels[index-1].pageSide = PageSide.CENTER
                        }
                    }
                    if (paginatedPanel.pageSide == paginatedPanels[index-1].pageSide){
                        paginatedPanels[index-1].pageSide = PageSide.CENTER
                    }
                }
            }
            if (paginatedPanels.last().pageSide == pageProgressionDirection.getFirstSide()){
                paginatedPanels.last().pageSide = PageSide.CENTER
            }
            paginatedPanels.forEach {
                spine += "<itemref idref=\"page_Images_${chapter.title}_${it.panel.image.toFile().nameWithoutExtension}\" properties=\"rendition:page-spread-${it.pageSide} rendition:${it.orientation}\"/>"
            }
        }
        return startPackage + metadata + startManifest + manifest + stopManifest + startSpine + spine + stopSpine + stopPackage
    }

    /**
     * Build the default manifest XML file content
     * @return [String] A formatted string containing the XML file content
     */
    protected fun buildXML() = """
        <?xml version="1.0"?>
        <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
        <rootfiles>
        <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
        </rootfiles>
        </container>
    """.trimIndent()
}