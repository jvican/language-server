package scala.meta.languageserver.providers

import com.typesafe.scalalogging.LazyLogging
import scala.meta.languageserver.Uri
import scala.meta.languageserver.search.SymbolIndex
import scala.meta.languageserver.ScalametaEnrichments._
import org.langmeta.lsp.DocumentHighlight
import org.langmeta.lsp.Position

object DocumentHighlightProvider extends LazyLogging {

  def highlight(
      symbolIndex: SymbolIndex,
      uri: Uri,
      position: Position
  ): List[DocumentHighlight] = {
    logger.info(s"Document highlight in $uri")
    for {
      data <- symbolIndex.findReferences(uri, position.line, position.character)
      _ = logger.info(s"Highlighting symbol `${data.name}: ${data.signature}`")
      pos <- data.referencePositions(withDefinition = true)
      if pos.uri == uri.value
      _ = logger.debug(s"Found highlight at [${pos.range.get.pretty}]")
      // TODO(alexey) add DocumentHighlightKind: Text (default), Read, Write
    } yield DocumentHighlight(pos.range.get.toRange)
  }

}
