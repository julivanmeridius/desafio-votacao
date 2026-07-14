package br.com.company.votacao.constants;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VotacaoConstantsTest {

    @Test
    void constructor_isPrivateAndInstantiable() throws Exception {
        var constructor = VotacaoConstants.class.getDeclaredConstructor();
        assertThat(constructor.canAccess(null)).isFalse();
        constructor.setAccessible(true);
        var instance = constructor.newInstance();
        assertThat(instance).isInstanceOf(VotacaoConstants.class);
    }
}
