/*
 * Copyright (c) 2017 Minkyu Cho
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.pitzcarraldo.spring.view.node

import java.util
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.springframework.web.servlet.view.AbstractTemplateView

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
class NodeView extends AbstractTemplateView {
  @BeanProperty
  var viewPath: String = _
  @BeanProperty
  var renderer: NodeViewRenderer = _

  override def renderMergedTemplateModel(
                                          model: util.Map[String, AnyRef],
                                          httpRequest: HttpServletRequest,
                                          httpResponse: HttpServletResponse): Unit = {
    httpResponse.setContentType(getContentType)
    model.remove("springMacroRequestContext")
    val writer = httpResponse.getWriter
    try {
      val viewFilePath = httpRequest.getServletContext.getResource(viewPath).getPath
      val template: NodeViewTemplate = new NodeViewTemplate(viewFilePath, model)
      val response: util.Map[String, AnyRef] = renderer.render(template)
      if (response.containsKey("headers")) {
        val headers = response.get("headers").asInstanceOf[util.Map[String, String]].asScala
        headers.foreach(header => httpResponse.setHeader(header._1, header._2))
      }
      writer.append(response.get("body").asInstanceOf[String])
    } catch {
      case e: Exception =>
        throw e
    } finally {
      writer.flush()
    }
  }
}