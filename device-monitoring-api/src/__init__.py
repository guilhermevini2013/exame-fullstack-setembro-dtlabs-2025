from flask import Flask
from .controllers.device import bp_device as device_bp
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker


DATABASE_URL = "postgresql://admin:admin@localhost:5432/device_monitoring"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def create_app():
    app = Flask(__name__)
    # routes
    app.register_blueprint(device_bp, url_prefix="/api")
    Base.metadata.create_all(bind=engine)

    return app
