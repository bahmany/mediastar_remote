from __future__ import annotations

import json
from typing import Any, Mapping

from app.stb.constants import (
    GMS_MSG_REQUEST_CHANNEL_LIST,
    GMS_MSG_REQUEST_LOGIN_INFO,
    GMS_MSG_DO_CHANNEL_FAV_MARK,
    GMS_MSG_DO_FAV_GROUP_RENAME,
    GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED,
    GMS_MSG_DO_REMOTE_CONTROL,
    GMS_MSG_DO_CHANNEL_SWITCH,
    GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET,
    GMS_MSG_REQUEST_PROGRAM_EPG,
)


def build_socket_frame(payload: bytes) -> bytes:
    length_str = f"{len(payload):07d}".encode("ascii")
    return b"Start" + length_str + b"End" + payload


def serialize_xml_command(request_type: int, items: list[Any] | None) -> str:
    parts: list[str] = [
        "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>",
        f"<Command request=\"{request_type}\">",
    ]

    if items is not None:
        if request_type == GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET and items:
            item0 = items[0]
            if isinstance(item0, Mapping) and "KeyCode" in item0:
                key_code = item0["KeyCode"]
            else:
                key_code = item0
            parts.append(f"<KeyCode>{_xml_escape(str(key_code))}</KeyCode>")

        # Channel list request (type 0) - FromIndex/ToIndex in parm
        elif request_type == GMS_MSG_REQUEST_CHANNEL_LIST and len(items) >= 2:
            parts.append("<parm>")
            parts.append(f"<FromIndex>{items[0].get('FromIndex', 0)}</FromIndex>")
            parts.append(f"<ToIndex>{items[1].get('ToIndex', 0)}</ToIndex>")
            parts.append("</parm>")

        elif request_type == GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED and items:
            item0 = items[0]
            if isinstance(item0, Mapping):
                if "IsFavList" in item0:
                    parts.append(f"<IsFavList>{_xml_escape(str(item0['IsFavList']))}</IsFavList>")
                if "SelectListType" in item0:
                    parts.append(f"<SelectListType>{_xml_escape(str(item0['SelectListType']))}</SelectListType>")

        elif request_type == GMS_MSG_DO_CHANNEL_FAV_MARK and items:
            item0 = items[0]
            if isinstance(item0, Mapping):
                parts.append(f"<TvState>{_xml_escape(str(item0.get('TvState', 0)))}</TvState>")
                parts.append(f"<FavMark>{_xml_escape(str(item0.get('FavMark', 0)))}</FavMark>")
                parts.append(f"<FavorGroupID>{_xml_escape(str(item0.get('FavorGroupID', '')))}</FavorGroupID>")

                program_ids = item0.get("ProgramIds")
                if isinstance(program_ids, list):
                    for pid in program_ids:
                        parts.append(f"<ProgramId>{_xml_escape(str(pid))}</ProgramId>")

                if "TotalNum" in item0 and item0["TotalNum"] is not None:
                    parts.append(f"<TotalNum>{_xml_escape(str(item0['TotalNum']))}</TotalNum>")
        
        # Remote control (type 1040) - KeyValue directly
        elif request_type == GMS_MSG_DO_REMOTE_CONTROL:
            for item in items:
                if isinstance(item, Mapping) and "KeyValue" in item:
                    parts.append(f"<KeyValue>{_xml_escape(str(item['KeyValue']))}</KeyValue>")
                else:
                    parts.append(f"<KeyValue>{_xml_escape(str(item))}</KeyValue>")
        
        # Channel switch (type 1000) - TvState + ProgramId in parm
        elif request_type == GMS_MSG_DO_CHANNEL_SWITCH:
            for item in items:
                parts.append("<parm>")
                if isinstance(item, Mapping):
                    if "TvState" in item:
                        parts.append(f"<TvState>{_xml_escape(str(item['TvState']))}</TvState>")
                    if "ProgramId" in item:
                        parts.append(f"<ProgramId>{_xml_escape(str(item['ProgramId']))}</ProgramId>")
                parts.append("</parm>")
        
        # EPG request (type 5) - ProgramId in parm
        elif request_type == GMS_MSG_REQUEST_PROGRAM_EPG:
            for item in items:
                parts.append("<parm>")
                if isinstance(item, Mapping) and "ProgramId" in item:
                    parts.append(f"<ProgramId>{_xml_escape(str(item['ProgramId']))}</ProgramId>")
                parts.append("</parm>")
        
        # Login request (type 998) - data/uuid as Map
        elif request_type == GMS_MSG_REQUEST_LOGIN_INFO:
            for item in items:
                if isinstance(item, Mapping):
                    for k, v in item.items():
                        parts.append(f"<{k}>{_xml_escape(str(v))}</{k}>")
        
        # Default: output key-value pairs
        else:
            for item in items:
                if isinstance(item, Mapping):
                    for k, v in item.items():
                        parts.append(f"<{k}>{_xml_escape(str(v))}</{k}>")
                else:
                    parts.append(f"<KeyValue>{_xml_escape(str(item))}</KeyValue>")

    parts.append("</Command>")
    return "".join(parts)


def serialize_json_command(request_type: int, items: list[Any] | None) -> str:
    root: dict[str, Any] = {"request": str(request_type)}

    if items is None:
        return json.dumps(root, separators=(",", ":"))

    if request_type == GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET and items:
        item0 = items[0]
        if isinstance(item0, Mapping) and "KeyCode" in item0:
            root["KeyCode"] = str(item0["KeyCode"])
        else:
            root["KeyCode"] = str(item0)
        return json.dumps(root, separators=(",", ":"))

    if request_type == GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED and items:
        item0 = items[0]
        if isinstance(item0, Mapping):
            if "IsFavList" in item0:
                root["IsFavList"] = str(item0["IsFavList"])
            if "SelectListType" in item0:
                root["SelectListType"] = str(item0["SelectListType"])
        return json.dumps(root, separators=(",", ":"))

    if request_type == GMS_MSG_DO_FAV_GROUP_RENAME and items:
        item0 = items[0]
        if isinstance(item0, Mapping):
            if "FavorRenamePos" in item0:
                root["FavorRenamePos"] = str(item0["FavorRenamePos"])
            if "FavorNewName" in item0:
                root["FavorNewName"] = str(item0["FavorNewName"])
            if "FavorGroupID" in item0:
                root["FavorGroupID"] = str(item0["FavorGroupID"])
        return json.dumps(root, separators=(",", ":"))

    if request_type == GMS_MSG_DO_CHANNEL_FAV_MARK and items:
        item0 = items[0]
        if isinstance(item0, Mapping):
            root["TvState"] = str(item0.get("TvState", 0))
            root["FavMark"] = str(item0.get("FavMark", 0))
            root["FavorGroupID"] = str(item0.get("FavorGroupID", ""))

            program_ids = item0.get("ProgramIds")
            array: list[dict[str, str]] = []
            if isinstance(program_ids, list):
                for pid in program_ids:
                    array.append({"ProgramId": str(pid)})
            root["array"] = array

            if "TotalNum" in item0 and item0["TotalNum"] is not None:
                root["TotalNum"] = str(item0["TotalNum"])

        return json.dumps(root, separators=(",", ":"))

    # Channel list request (type 0) - FromIndex/ToIndex directly in root
    if request_type == GMS_MSG_REQUEST_CHANNEL_LIST and len(items) >= 2:
        root["FromIndex"] = str(items[0].get("FromIndex", 0))
        root["ToIndex"] = str(items[1].get("ToIndex", 0))
        return json.dumps(root, separators=(",", ":"))
    
    # Remote control (type 1040) - array of KeyValue objects
    if request_type == GMS_MSG_DO_REMOTE_CONTROL:
        array: list[dict] = []
        for item in items:
            if isinstance(item, Mapping) and "KeyValue" in item:
                array.append({"KeyValue": str(item["KeyValue"])})
            else:
                array.append({"KeyValue": str(item)})
        root["array"] = array
        return json.dumps(root, separators=(",", ":"))
    
    # Channel switch (type 1000) - array of TvState/ProgramId objects
    if request_type == GMS_MSG_DO_CHANNEL_SWITCH:
        array = []
        for item in items:
            obj = {}
            if isinstance(item, Mapping):
                if "TvState" in item:
                    obj["TvState"] = str(item["TvState"])
                if "ProgramId" in item:
                    obj["ProgramId"] = str(item["ProgramId"])
            array.append(obj)
        root["array"] = array
        return json.dumps(root, separators=(",", ":"))
    
    # EPG request (type 5) - array of ProgramId objects
    if request_type == GMS_MSG_REQUEST_PROGRAM_EPG:
        array = []
        for item in items:
            if isinstance(item, Mapping) and "ProgramId" in item:
                array.append({"ProgramId": str(item["ProgramId"])})
        root["array"] = array
        return json.dumps(root, separators=(",", ":"))
    
    # Login request (type 998) - array of key-value objects
    if request_type == GMS_MSG_REQUEST_LOGIN_INFO:
        array = []
        for item in items:
            if isinstance(item, Mapping):
                obj = {k: str(v) for k, v in item.items()}
                array.append(obj)
        root["array"] = array
        return json.dumps(root, separators=(",", ":"))
    
    # Default: put items in array
    array = []
    for item in items:
        if isinstance(item, Mapping):
            array.append({k: str(v) for k, v in item.items()})
        else:
            array.append({"KeyValue": str(item)})
    root["array"] = array
    return json.dumps(root, separators=(",", ":"))


def _xml_escape(s: str) -> str:
    return (
        s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
        .replace("'", "&apos;")
    )
