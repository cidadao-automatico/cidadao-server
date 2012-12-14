package models

case class PLPage(page:Long, pages: Long, perpage: Int, total: Long, content: Seq[ProjetoLei])

