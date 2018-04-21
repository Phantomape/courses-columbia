from flask import Flask
from flask import request, g, send_from_directory
from routes import *

app = Flask(__name__, static_url_path='')
app.register_blueprint(routes)


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

