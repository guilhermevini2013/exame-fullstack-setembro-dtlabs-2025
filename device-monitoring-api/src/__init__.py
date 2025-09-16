from flask import Flask
from .controllers.device import bp_device as device_bp

def create_app():
    app = Flask(__name__)
    # routes
    app.register_blueprint(device_bp, url_prefix="/api")

    return app
