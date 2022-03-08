package com.craft.complaint;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Import;
import com.craft.complaint.common.utils.*;

@AutoConfigurationPackage
@Import({AsyncRunner.class})
public class LibAutoConfiguration {
}
