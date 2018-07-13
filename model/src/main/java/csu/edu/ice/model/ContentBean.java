package csu.edu.ice.model;

import java.util.List;
import java.util.Map;

public class ContentBean {

    /**
     * blocks : [{"key":"d5l3p","text":"123","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"9f3dc","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"3vq85","text":"xxxx","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"fgknl","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"uhbi","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"dk450","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"13li4","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"3cjrf","text":" ","type":"atomic","depth":0,"inlineStyleRanges":[],"entityRanges":[{"offset":0,"length":1,"key":0}],"data":{}},{"key":"e783e","text":" ","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"6dsjs","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"6g1l5","text":"dsadasd","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}},{"key":"a2oet","text":" ","type":"atomic","depth":0,"inlineStyleRanges":[],"entityRanges":[{"offset":0,"length":1,"key":1}],"data":{}},{"key":"dpbis","text":"","type":"unstyled","depth":0,"inlineStyleRanges":[],"entityRanges":[],"data":{}}]
     * entityMap : {"0":{"type":"IMAGE","mutability":"IMMUTABLE","data":{"url":"blob:http://localhost:3000/4c13fb73-0e7c-4a9b-b0c1-f1ef81e1fb00","name":"QQ20180526-104511@2x.png","type":"IMAGE","meta":{}}},"1":{"type":"IMAGE","mutability":"IMMUTABLE","data":{"url":"blob:http://localhost:3000/aa50a2b2-3fba-4164-bdfd-1b334176671e","name":"QQ20180526-104511@2x.png","type":"IMAGE","meta":{}}}}
     */

    private Map<String,EntityBean> entityMap;
    private List<BlocksBean> blocks;

    public List<BlocksBean> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlocksBean> blocks) {
        this.blocks = blocks;
    }

    public Map<String, EntityBean> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<String, EntityBean> entityMap) {
        this.entityMap = entityMap;
    }

    public class EntityBean {

        private String type;
        private String mutability;
        private DataBean data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMutability() {
            return mutability;
        }

        public void setMutability(String mutability) {
            this.mutability = mutability;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public class DataBean {
            /**
             * url : blob:http://localhost:3000/4c13fb73-0e7c-4a9b-b0c1-f1ef81e1fb00
             * name : QQ20180526-104511@2x.png
             * type : IMAGE
             * meta : {}
             */

            private String url;
            private String name;
            private String type;
            private MetaBean meta;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public MetaBean getMeta() {
                return meta;
            }

            public void setMeta(MetaBean meta) {
                this.meta = meta;
            }

            public class MetaBean {
            }
        }
    }


    public class BlocksBean {

        private String key;
        private String text;
        private String type;
        private int depth;
        private DataBean data;
        private List<?> inlineStyleRanges;
        private List<EntityRangesBean> entityRanges;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public List<?> getInlineStyleRanges() {
            return inlineStyleRanges;
        }

        public void setInlineStyleRanges(List<?> inlineStyleRanges) {
            this.inlineStyleRanges = inlineStyleRanges;
        }

        public List<EntityRangesBean> getEntityRanges() {
            return entityRanges;
        }

        public void setEntityRanges(List<EntityRangesBean> entityRanges) {
            this.entityRanges = entityRanges;
        }

        public class DataBean {
        }

        public class EntityRangesBean {
            /**
             * offset : 0
             * length : 1
             * key : 0
             */

            private int offset;
            private int length;
            private int key;

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }

            public int getKey() {
                return key;
            }

            public void setKey(int key) {
                this.key = key;
            }
        }
    }
}
