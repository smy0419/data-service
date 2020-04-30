package network.asimov.response;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-03-05
 */
@Data
public class PageView<T> {
    @ApiModelProperty(value= "Total Count")
    private long total;
    @ApiModelProperty(value= "Result list")
    private List<T> items;

    public static <T> PageView<T> of(long total, List<T> result) {
        PageView<T> pageView = new PageView<>();
        pageView.setItems(result);
        pageView.setTotal(total);
        return pageView;
    }

    public static <T> PageView<T> empty() {
        return PageView.of(0, Lists.newArrayList());
    }
}
