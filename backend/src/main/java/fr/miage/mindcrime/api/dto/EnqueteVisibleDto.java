package fr.miage.mindcrime.api.dto;

import fr.miage.mindcrime.moteur.modele.Enquete;
import fr.miage.mindcrime.moteur.modele.Lieu;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record EnqueteVisibleDto(
        String id,
        String partieId,
        long seed,
        CrimeDto crime,
        List<SuspectDto> suspects,
        List<LieuDto> lieux,
        List<IndiceVisibleDto> indicesVisibles,
        String statutUnicite
) {
    public static EnqueteVisibleDto depuisDomaine(Enquete enquete) {
        Map<String, String> nomsLieux = enquete.lieux().stream()
                .collect(Collectors.toMap(Lieu::id, Lieu::nom));

        CrimeDto crimeDto = new CrimeDto(
                enquete.crime().lieuId(),
                nomsLieux.getOrDefault(enquete.crime().lieuId(), enquete.crime().lieuId()),
                enquete.crime().creneau().ordre(),
                enquete.crime().creneau().libelle()
        );

        List<SuspectDto> suspectsDto = enquete.suspects().stream()
                .map(s -> new SuspectDto(s.id(), s.nom(), s.profil()))
                .toList();

        List<LieuDto> lieuxDto = enquete.lieux().stream()
                .map(l -> new LieuDto(l.id(), l.nom()))
                .toList();

        List<IndiceVisibleDto> indicesDto = enquete.indicesVisibles().stream()
                .map(i -> new IndiceVisibleDto(
                        i.id(),
                        i.type().name(),
                        i.suspectId(),
                        i.lieuId(),
                        i.creneau().libelle(),
                        i.description()
                ))
                .toList();

        return new EnqueteVisibleDto(
                enquete.id(),
                enquete.partieId(),
                enquete.seed(),
                crimeDto,
                suspectsDto,
                lieuxDto,
                indicesDto,
                enquete.statutUnicite().name()
        );
    }
}