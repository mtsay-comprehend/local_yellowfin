package com.comprehend.yfws_client

import java.io.File
import java.nio.file.Files
import java.util.Base64

import com.hof.mi.web.service.{ContentResource, ImportOption}
import com.typesafe.config.ConfigFactory

import scala.language.postfixOps

object Main {
  val client = YFWSClient.fromConfig(ConfigFactory.load())

  val contentXml = "YFExport.xml"

  val affectedReports = List(
    "Queries by Current Status",
    "Open Queries by Age",
    "Open Query Rate: Site vs. Study Average",
    "Query Resolution Summary",
    "Query Average Resolution Cycle Time Rate: Site vs. Study Average",
    "Open Query Rate Summary",
    "Query Rate: Site vs. Study Average",
    "Open Queries by Age Trend",
    "Open Queries by Age Rate: Site vs. Study Average",
    "Queries - Listing",
    "Open Queries By Month - Listing",
    "Query Rate Summary",
    "Open Query - Listing",
    "Open Queries by Age Summary",
    "Open Queries - Listing",
    "Closed Queries - Listing",
    "Cancelled Queries - Listing",
    "Query Rate by eCRF",
    "Answered Queries - Listing"
  )

  def main(args: Array[String]): Unit = {
    importContentXml
  }

  def importContentXml: Unit = {
    val oldContent = client.call("GETCONTENT") {
      identity
    } getContentResources

    val studyQueriesID = oldContent.find(_.getResourceName == "Study Queries")
      .map(_.getResourceId)
      .getOrElse(throw new IllegalStateException("Unable to find Study Queries view in Yellowfin."))

    val importXml = getContent(contentXml)

    val newContent = client.call("GETIMPORTCONTENT") { req =>
      req.setParameters(Array(importXml, "xml"))
      req
    } getContentResources

    val importOptions = (newContent map { newResource =>
      oldContent.find(_.getResourceUUID == newResource.getResourceUUID) match {
        case _ if newResource.getResourceName == "Study Queries" =>
          println(s"Skipping ${newResource.getResourceUUID} ${newResource.getResourceName}")

          Map(
            "SKIP" -> "True"
          )
        case Some(oldResource) =>
          println(s"Replacing ${newResource.getResourceUUID} ${newResource.getResourceName}")

          Map(
            "SKIP" -> "False",
            "OPTION" -> "REPLACE",
            "EXISTING" -> {
              oldResource.getResourceType match {
                case "RPTCATEGORY" | "RPTSUBCATEGORY" => oldResource.getResourceCode
                case _ => oldResource.getResourceId.toString
              }
            }
          ) ++ {
            // also update the view to point at the existing Study Queries if the report is based on Study Queries
            if (affectedReports.contains(newResource.getResourceName)) Some("VIEW" -> s"VIEW$studyQueriesID")
            else None
          }
        case None =>
          println(s"Adding ${newResource.getResourceUUID} ${newResource.getResourceName}")
          Map(
            "SKIP" -> "False",
            "OPTION" -> "ADD"
          )
      }
    } zipWithIndex) flatMap {
      case (keyValues, index) =>
        keyValues map { toImportOption(_, index) }
    }

    client.call("IMPORTCONTENT") { req =>
      req.setParameters(Array(importXml, "xml"))
      req.setImportOptions(importOptions)
      req
    }
  }

  def toImportOption(keyValue: (String, String), index: Int): ImportOption = {
    val importOption = new ImportOption
    importOption.setOptionKey(keyValue._1)
    importOption.setOptionValue(keyValue._2)
    importOption.setItemIndex(index)
    importOption
  }

  val contentResourceFields: Map[String, ContentResource => Any] = Map(
    ("UUID", _.getResourceUUID),
    ("ID", _.getResourceId),
    ("Type", _.getResourceType),
    ("Name", _.getResourceName),
    ("Description", _.getResourceDescription),
    ("OrgId", _.getResourceOrgId),
    ("Code", _.getResourceCode)
  )

  def printToCSV(resource: ContentResource): Unit = {
    println(contentResourceFields.keys mkString ",")
    println(contentResourceFields.values map { _ apply resource } mkString ",")
  }

  def printContentResource(resource: ContentResource): Unit = {
    contentResourceFields foreach {
      case (fieldName, fieldAccessor) =>
        println(s"$fieldName: ${fieldAccessor apply resource}")
    }
  }

  def getContent(filename: String): String = {
    new String(Base64.getEncoder.encode(Files.readAllBytes(new File(filename).toPath)))
  }

  def flushCachedFilterCache: Unit = {
    client.call("FLUSHCACHEDFILTERCACHE") { req =>
      req.setParameters(Array())
      req
    }
  }

  def listClients: Unit = {
    val response = client.call("LISTCLIENTS") {
      identity
    }

    response.getClients foreach { clientOrg =>
      print(
        s"""
           |ID: ${clientOrg.getClientId}
           |Name: ${clientOrg.getClientName}
           |Reference ID: ${clientOrg.getClientReferenceId}
        """.stripMargin
      )
    }
  }

  def listGroups(clientId: String): Unit = {
    val response = client.call("LISTGROUPS") { req =>
      req.setOrgRef(clientId)
      req
    }

    response.getGroups foreach { group =>
      print(
        s"""
          |ID: ${group.getGroupId}
          |Name: ${group.getGroupName}
          |Description: ${group.getGroupDescription}
          |Internal Reference: ${group.getGroupInternalReference}
        """.stripMargin
      )
    }
  }

  def updateGroupName(groupId: String, groupName: String): Unit = {
    client.runQuery(
      s"""
         |UPDATE accessgroup
         |SET shortdescription = '$groupName'
         |WHERE internalreferenceid = '$groupId'
      """.stripMargin)
  }
}
