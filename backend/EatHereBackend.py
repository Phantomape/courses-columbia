from flask import Flask
from flask import request, g, send_from_directory
from routes import *

app = Flask(__name__, static_url_path='')
app.register_blueprint(routes)
# engine = connectdb()  # get connection


# @app.before_request
# def before_request():
#   """
#   This function is run at the beginning of every web request
#   (every time you enter an address in the web browser).
#   We use it to setup a database connection that can be used throughout the request
#   The variable g is globally accessible
#   """
#   try:
#     g.conn = engine.connect()
#     print "Connected to Database"
#   except:
#     print "uh oh, problem connecting to database"
#     import traceback; traceback.print_exc()
#     g.conn = None


# @app.teardown_request
# def teardown_request(exception):
#   """
#   At the end of the web request, this makes sure to close the database connection.
#   If you don't the database could run out of memory!
#   """
#   try:
#     g.conn.close()
#     print "Disconnected with Database"
#   except Exception as e:
#     pass


@app.route('/img/<path:path>')
def img(path):
    return send_from_directory('static/img', path)


if __name__ == "__main__":

    import click
    @click.command()
    @click.option('--debug', is_flag=False)
    @click.argument('HOST', default='0.0.0.0')
    @click.argument('PORT', default=8080, type=int)
    def run(debug, host, port):
        HOST, PORT = host, port
        print "running on %s:%d" % (HOST, PORT)
        app.run(host=HOST, port=PORT, debug=True, threaded=True)
    run()

