package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaAvatar
import com.pixamob.pixacompose.components.PixaAvatarSize
import com.pixamob.pixacompose.image.PixaImage
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

/** A tiny gradient PNG, so the showcase and its snapshots need no network. */
internal val SampleImage: ByteArray =
    ("89504e470d0a1a0a0000000d4948445200000018000000100802000000834628c20000038b4944415478da0dccb10a6e500000e0fb3877331a8d469bd16834ca20750606250b37ca29118a883a8a3a3190410cea0c8a5206cf71ffef01be3f7f878f1a5e7a7898e16287931b083f1cc2b08bc32a0d8b3c4cda30ea03b686ce1d5a3834c95095438e860c0ff13c84db1090c1bf877f7ffef61fd5bf74ff30fdc5f627d713be3f847e17fb55ea17b99fb47ed47b6cf59ddbb7b06f92be2afb1cf519eee3b90fb73e20bd7ff7bf087d147a69f430e862d1c921c2a34340bb8856092d329a3434ea085ba873510b5193a0aa4439421946f18cc20d0504f937fa45f547d52f5d3f4c7db1f5c9d584af0fa1dec57a95ea45ae27ad1ef51a5b75e7d62dac9ba4aeca3a477586eb78aec3ad0e48eddff52f2a3eaa78e9e2618a8b2d4eae207c7108c52e16ab542c723169c5a817d82a3ab76861d124455516392a325cc473116e45400aff2e7e51fa51e94ba70f935e6c7a7229e1d3434877315da57491d3494b473dc556dab9690bd32649ab32cd519ae1349ed3704b0392fa77fa8ba28f8a5e3a7a98e862a3938b081f1d42b48bd12a458b1c4d5a34ea11b6a2ce8d5a1835495495518ea20c47f11c855b1490c8bfa35f043f0abe347c1878b1f0e420e1e121c05d84ab0417194e1a1c75882dd8b9b085b0496055c21cc10cc37886e1060302fd1bfe22efa3bc97f61ec6bb58efe43cc27b87e0eda2b74ade227b93e68dba872daf73bd167a4de255a597232fc35e3c7be1e605c4f36fef17391fe5bcb4f330cec53a27e710de390467179d557216d9993467d41d6c399debb4d06912a72a9d1c391976e2d9093727208e7f3bbfc8fe28fba5ed87b12fd63e399bf0f621d8bb68af92bdc8f6a4d9a36e63cbee5cbb857693d85569e7c8ceb01dcf76b8d901b1fddbfe45e647992f6d3e8c79b1e6c99984370fc1dc457395cc453627cd1c75135b66e79a2d349bc4ac4a33476686cd7836c3cd0c88e9dfe62f323eca7869e3618c8b354ece20bc7108c62e1aab642cb23169c6a81bd8323ad768a1d12446551a3932326cc4b3116e46400cff367e11f828f0d2e061c0c5829303840787007611ac125864306960d401b640e7821682260155097204320ce219841b0808f06ff08bd48f525f5a7d18f562d5935309af1e82ba8bea2aa98bac4e9a3aea2ab6d4ce555ba836895a956a8ed40cabf1ac869b1a10d5bfd55fa47c94f2d2cac32817ab9c9c4278e510945d545649596465d2945157b0a574aed242a54994aa5472a46458896725dc948028feadfcfb0f0388c6c79a675d710000000049454e44ae426082")
        .chunked(2).map { it.toInt(16).toByte() }.toByteArray()

/** [model] is anything Coil can load; the snapshot test passes a URL to cover the network path. */
@Composable
fun ImageSection(model: Any? = SampleImage) {
    PixaText("Image", style = PixaTheme.typography.h3)
    PixaImage(model, contentDescription = "Sample", modifier = Modifier.fillMaxWidth().aspectRatio(21f / 9f))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        PixaImage(model, contentDescription = "Sample", modifier = Modifier.size(64.dp), shape = PixaTheme.shapes.full)
        // Components nest through slots: an avatar holding an image.
        PixaAvatar(size = PixaAvatarSize.Lg) {
            PixaImage(model, contentDescription = "Sample", modifier = Modifier.matchParentSize(), shape = PixaTheme.shapes.full)
        }
        // No model: the error slot, first the default and then a custom one.
        PixaImage(null, contentDescription = null, modifier = Modifier.size(64.dp))
        PixaImage(null, contentDescription = null, modifier = Modifier.size(64.dp), error = {
            PixaIcon(PixaIcons.Close, contentDescription = "Failed", modifier = Modifier.align(Alignment.Center))
        })
    }
}
