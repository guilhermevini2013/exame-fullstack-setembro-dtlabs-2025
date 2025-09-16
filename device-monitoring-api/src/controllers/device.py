from flask import Blueprint, jsonify, request

bp_device = Blueprint("device", __name__)

@bp_device.route("/", methods=["GET"])
def hello():
    return jsonify()
