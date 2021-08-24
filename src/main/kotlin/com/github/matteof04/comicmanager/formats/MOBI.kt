/*
 * Copyright (C) 2021 Matteo Franceschini <matteof5730@gmail.com>
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

import com.lordcodes.turtle.shellRun
import com.sksamuel.scrimage.ImmutableImage
import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.util.*
import com.github.matteof04.comicmanager.util.Panel
import com.github.matteof04.comicmanager.util.PanelMetadata
import com.github.matteof04.comicmanager.util.mkdir
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.copyTo
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
/**
 * The class for the MOBI format, a custom Kindle format
 * This class will not work if there's no KindleGen executable in the PATH
 */
class MOBI(path: Path, bookTitle: String, author: String, pageProgressionDirection: PageProgressionDirections, deviceInformation: DeviceInformation) : EPUB(path, bookTitle, author,
    pageProgressionDirection, deviceInformation){

    override fun addChapter(chapter: Chapter) {
        chapters.add(chapter)
        val imageDir = images.resolve(chapter.title)
        val textDir = text.resolve(chapter.title)
        imageDir.mkdir()
        textDir.mkdir()
        chapter.pages.forEach {
            it.image.copyTo(imageDir.resolve(it.image.name))
            textDir.resolve(it.image.toFile().nameWithoutExtension+".xhtml").toFile().writeText(buildHTML(chapter.title, it.image.name, ImmutableImage.loader().fromPath(it.image).width, ImmutableImage.loader().fromPath(it.image).height, it.metadata == PanelMetadata.ROTATED))
        }
    }

    /**
     * @see com.github.matteof04.comicmanager.formats.EPUB.buildHTML
     */
    private fun buildHTML(chapterTitle: String, pageName: String, width: Int, height: Int, rotated: Boolean): String {
        val startHTML = """
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
            """.trimIndent()
        val stopHTML = """
                </body>
                </html>
            """.trimIndent()
        var html = ""
        val noHorizontalPV = (width-deviceInformation.resolution.width) < (width*0.01)
        val noVerticalPV = (height-deviceInformation.resolution.height) < (height*0.01)
        var order = IntArray(4)
        var boxes = arrayListOf<PanelViewBoxes>()
        if (!noHorizontalPV && !noVerticalPV){
            order = if (rotated){
                when(pageProgressionDirection){
                    PageProgressionDirections.RIGHT_TO_LEFT -> arrayOf(1, 3, 2, 4).toIntArray()
                    PageProgressionDirections.LEFT_TO_RIGHT -> arrayOf(2, 4, 1, 3).toIntArray()
                }
            }else{
                when(pageProgressionDirection){
                    PageProgressionDirections.RIGHT_TO_LEFT -> arrayOf(2, 1, 4, 3).toIntArray()
                    PageProgressionDirections.LEFT_TO_RIGHT -> arrayOf(1, 2, 3, 4).toIntArray()
                }
            }

            boxes = PanelViewBoxes.getTBLRBoxes()
        }else if(noHorizontalPV && !noVerticalPV){
            order = if (rotated){
                when(pageProgressionDirection){
                    PageProgressionDirections.RIGHT_TO_LEFT -> arrayOf(1, 2).toIntArray()
                    PageProgressionDirections.LEFT_TO_RIGHT -> arrayOf(2, 1).toIntArray()
                }
            }else{
                arrayOf(1, 2).toIntArray()
            }
            boxes = PanelViewBoxes.getTBBoxes()
        }else if(!noHorizontalPV && noVerticalPV){
            order = if (rotated){
                arrayOf(1, 2).toIntArray()
            }else{
                when(pageProgressionDirection){
                    PageProgressionDirections.RIGHT_TO_LEFT -> arrayOf(2, 1).toIntArray()
                    PageProgressionDirections.LEFT_TO_RIGHT -> arrayOf(1, 2).toIntArray()
                }
            }
            boxes = PanelViewBoxes.getLRBoxes()
        }
        boxes.forEachIndexed { index, panelViewBox ->
            html += """
                <div id="$panelViewBox">
                <a style="display:inline-block;width:100%;height:100%;" class="app-amzn-magnify" "data-app-amzn-magnify='{"targetId":"$panelViewBox-P", "ordinal":${order[index]}}'></a>",
                </div>
            """.trimIndent()
        }
        boxes.forEach {
            html += """
                <div class="PV-P" id="$it-P">
                <img style="${it.getStyle(width, height, deviceInformation)}" src="../../Images/"$chapterTitle"/"$pageName" width="$width" height="$height"/>
                </div>
            """.trimIndent()
        }
        return startHTML+html+stopHTML
    }

    override fun buildCSS(): String {
        return if (deviceInformation.supportPanelView){
            super.buildCSS()+"""
                #PV {
                    position: absolute;
                    width: 100%;
                    height: 100%;
                    top: 0;
                    left: 0;
                }
                #PV-T {
                    top: 0;
                    width: 100%;
                    height: 50%;
                }
                #PV-B {
                    bottom: 0;
                    width: 100%;
                    height: 50%;
                }
                #PV-L {
                    left: 0;
                    width: 49.5%;
                    height: 100%;
                    float: left;
                }
                #PV-R {
                    right: 0;
                    width: 49.5%;
                    height: 100%;
                    float: right;
                }
                #PV-TL {
                    top: 0;
                    left: 0;
                    width: 49.5%;
                    height: 50%;
                    float: left;
                }
                #PV-TR {
                    top: 0;
                    right: 0;
                    width: 49.5%;
                    height: 50%;
                    float: right;
                }
                #PV-BL {
                    bottom: 0;
                    left: 0;
                    width: 49.5%;
                    height: 50%;
                    float: left;
                }
                #PV-BR {
                    bottom: 0;
                    right: 0;
                    width: 49.5%;
                    height: 50%;
                    float: right;
                }
                .PV-P {
                    width: 100%;
                    height: 100%;
                    top: 0;
                    position: absolute;
                    display: none;
                }
            """.trimIndent()
        }else{
            super.buildCSS()
        }
    }
    override fun buildOPF(): String {        val startPackage = """
            <?xml version="1.0" encoding="UTF-8"?>
            <package version="3.0" unique-identifier="BookID" xmlns="http://www.idpf.org/2007/opf">
        """.trimIndent()
        val metadata = """
            <metadata xmlns:opf="http://www.idpf.org/2007/opf" xmlns:dc="http://purl.org/dc/elements/1.1/">
            <dc:title>$bookTitle</dc:title>
            <dc:language>en-US</dc:language>
            <dc:identifier id="BookID">urn:uuid:$uuid</dc:identifier>
            <dc:contributor id="contributor">ComicManager</dc:contributor>
            <dc:creator>$author</dc:creator>
            <meta property="dcterms:modified">${LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))}</meta>
            <meta name="cover" content="cover"/>
            <meta name="fixed-layout" content="true"/>
            <meta name="original-resolution" content="${deviceInformation.resolution}"/>
            <meta name="book-type" content="comic"/>
            <meta name="primary-writing-mode" content="horizontal-${pageProgressionDirection.toString().replace("t", "")}"/>
            <meta name="zero-gutter" content="true"/>
            <meta name="zero-margin" content="true"/>
            <meta name="ke-border-color" content="#FFFFFF"/>
            <meta name="ke-border-width" content="0"/>
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
    override fun build(coverImage: Panel) {
        super.build(coverImage)
        if (checkKindleGen()){
            path.toFile().renameTo(File(path.nameWithoutExtension.plus(Formats.EPUB.extension)))
            shellRun(KindleGen, listOf("-dont_append_source", path.nameWithoutExtension.plus(Formats.EPUB.extension)))
        }
    }
    companion object {
        /**
         * Check if there's KindleGen executable in the path
         * @return [Boolean] True if there's, false if not
         */
        fun checkKindleGen() : Boolean{
            return try {
                shellRun(KindleGen, listOf("-locale", "en"))
                true
            }catch (e: Exception){
                false
            }
        }
        const val KindleGen = "kindlegen"
    }
}