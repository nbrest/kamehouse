# Logging Strategy:

### Backend logging strategy

The goals of logging in my application are being able to troubleshoot issues quickly when they happen and knowing exactly what's happening in the application.

* **TRACE** I need to be able to see what methods are called with what parameters on all layers to troubleshoot issues.

* **DEBUG** Responses from database or other external connections (except for example, vlcRcStatus, which is once a second, so its more sensible to put it on trace) on actions and tasks that I perform. For example adding a new VlcPlayer to the database or updating tables or using the video player from the ui.

* **INFO:** Application configuration information or general application status, not specific to a task. During startup or if I change an application setting dynamically.

* **WARN:** Only show behaviour that didn't go as expected but is recoverable and the process can continue. For example, user not found when trying to delete or load a user.

* **ERROR:** Unrecoverable unexpected behaviour. If I throw an exception, I usually need to log an ERROR too or in some cases a WARN. For example, when I don't get a response from VlcPlayer.

### Frontend logging strategy

It's pretty similar in concept to the backend strategy. The goals is still the same.

* **TRACE** I need to be able to see what methods/functions are called with what parameters. Its even easier to do in the frontend with my logging framework.

* **DEBUG** Responses from the backend or other external connections (except for example, vlcRcStatus, which is once a second, so its more sensible to put it on trace) on actions and tasks that I perform.

* **INFO:** Application configuration information or general application status, not specific to a task. During startup or if I change an application setting dynamically.

* **WARN:** Only show behaviour that didn't go as expected but is recoverable and the process can continue.

* **ERROR:** Unrecoverable unexpected behaviour. Error responses from the backend, for example.
