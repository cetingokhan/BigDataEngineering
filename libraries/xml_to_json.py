class XmlToJson:

    def __init__(self):
        pass

    def __xml_to_json_array(self, xml_element_array):
        result = []
        for item in xml_element_array:
            result.append(self.convert(item))
        return result

    def convert(self, xml_element):
        result = {}

        attributes = {}
        for att in xml_element.attrib:
            if 'http' in att:
                # exclude xtype attributes
                continue
            attributes[f"@{att}"] = xml_element.attrib[att]

        if len(xml_element.getchildren()) == 0:
            if len(attributes.keys()) > 0:
                result[xml_element.tag] = attributes
                result[xml_element.tag]["value"] = xml_element.text
            else:
                result[xml_element.tag] = xml_element.text

        items = []
        for ch in xml_element.getchildren():
            is_exist = [x for x in items if x['name'] == ch.tag]
            if len(is_exist) == 0:
                items.append({
                    "name": ch.tag,
                    "count": len(xml_element.findall(ch.tag))
                })

        for prp in items:
            if prp["count"] > 1:
                result[prp["name"]] = self.__xml_to_json_array(xml_element.findall(prp["name"]))
            else:
                if len(xml_element.find(prp["name"]).getchildren()) == 0:
                    prp_attributes = {}
                    for att in xml_element.find(prp["name"]).attrib:
                        if 'http' in att:
                            continue
                        prp_attributes[f"@{att}"] = xml_element.find(prp["name"]).attrib[att]

                    if len(prp_attributes.keys()) > 0:
                        result[prp["name"]] = prp_attributes
                        result[prp["name"]]["value"] = xml_element.find(prp["name"]).text
                    else:
                        result[prp["name"]] = xml_element.find(prp["name"]).text
                else:
                    result[prp["name"]] = self.convert(xml_element.find(prp["name"]))

        return result
