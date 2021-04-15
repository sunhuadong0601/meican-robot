package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/16 12:12 上午
 */
@Data
public class MultiCorpAddress {
    private String fullPlaceholder;
    private String placeholder;
    private boolean useCorpAddressRemark;
    private boolean useMultiCorpAddress;
    private List<AddressListBean> addressList;
    private List<?> otherList;
    private List<RecentListBean> recentList;

    @Data
    public static class AddressListBean {
        private FinalValueBean finalValue;
        private String name;
        private List<?> sub;

        @Data
        public static class FinalValueBean {
            private String pickUpLocation;
            private String uniqueId;

            @Override
            public String toString() {
                return JSON.toJSONString(this);
            }
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    @Data
    public static class RecentListBean {
        private String address;
        private String timestamp;
        private String lastUsedTime;
        private String remark;
        private Long corpAddressId;
        private Long userId;
        private String uniqueId;
        private String pickUpLocation;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
