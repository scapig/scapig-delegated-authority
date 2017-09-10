package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class TokenController  @Inject()(cc: ControllerComponents) extends AbstractController(cc) with CommonControllers {

  def token() = ???
}
