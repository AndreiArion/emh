package controllers

import play.api._
import play.api.mvc._

/**
 * Provide security features
 */
trait Secured {
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = {
    //Logger.info("Unauthorized access to " + request.uri + " , redirecting to " + routes.Application.login())
   // Results.Redirect(routes.Application.login()).withSession("before_auth_requested_url" -> request.uri)
    Results.Redirect("")
  }

  // ---

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result): Action[(Action[AnyContent], AnyContent)] = Security.Authenticated(username, onUnauthorized) {
    userId =>
      Action({
        request =>
          Logger.info("Authorized access to " + request.uri + " , for user " + userId)
          f(userId)(request)
      })
  }

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated[A](parser: BodyParser[A])(f: => String => Request[A] => Result) = Security.Authenticated(username, onUnauthorized) {
    user =>
      Action(parser)(request => f(user)(request))
  }
}

trait XStaffing {
  this: Controller with Secured=>
  def index = IsAuthenticated { userid => implicit request =>
    controllers.Assets.at("/public", "index").apply(request)
  }
}

object Application extends Controller with Secured with XStaffing



