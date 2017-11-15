package com.comprehend.test

import com.hof.mi.web.service._

import scala.util.Try

object Main {

  val adminService = new AdministrationServiceServiceLocator(
    "yellowfin-dev-1",
    7900,
    "/services/AdministrationService",
    false
  ).getAdministrationService

  val request1 = {
    val req = new AdministrationServiceRequest
    req.setLoginId("admin@yellowfin.com.au")
    req.setPassword("test")
    req.setOrgId(new Integer(1))
    req.setFunction("RELOADCODES")
    req.setParameters(Array("S1000"))
    req
  }

  val request2 = {
    val req = new AdministrationServiceRequest
    req.setLoginId("admin@yellowfin.com.au")
    req.setPassword("test")
    req.setOrgId(new Integer(1))
    req.setFunction("CREATECLIENT")

    val clientOrg = new AdministrationClientOrg
    clientOrg.setClientName("Foo")
    clientOrg.setClientReferenceId("FOO")
    clientOrg.setTimeZoneCode("GMT")
    clientOrg.setDefaultOrg(false)

    req.setClient(clientOrg)
    req
  }

  def main(args: Array[String]): Unit = {
    val response = Try {
      adminService.remoteAdministrationCall(request1)
    }

    println(response)
  }
}
