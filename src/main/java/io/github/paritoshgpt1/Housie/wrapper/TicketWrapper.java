package io.github.paritoshgpt1.Housie.wrapper;

import io.github.paritoshgpt1.Housie.model.Player;
import io.github.paritoshgpt1.Housie.model.Ticket;
import io.github.paritoshgpt1.Housie.repository.PlayerRepository;
import io.github.paritoshgpt1.Housie.repository.TicketRepository;
import io.github.paritoshgpt1.Housie.util.Tambola;
import io.github.paritoshgpt1.Housie.util.TambolaTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class TicketWrapper {

    private PlayerRepository playerRepository;
    private TicketRepository ticketRepository;

    public Player findPlayerByCode(String code) {
        return playerRepository.findPlayerByCode(code);
    }

    public TambolaTicket[] saveTickets(Player player, TambolaTicket[] tickets) {
        for (TambolaTicket ticket: tickets) {
            Ticket tmp = Ticket.builder()
                    .numbers(ticket.getDBValue())
                    .player(player)
                    .build();
            ticket.id = ticketRepository.save(tmp).getId();
        }
        System.out.println("inside saveTickets");
        System.out.println(Arrays.toString(tickets));
        return tickets;
    }

    public Map<String, Object> ticketDetails(int ticketNumber) {
        Map<String, Object> result = new HashMap<>();
        Optional<Ticket> ticket = ticketRepository.findById(ticketNumber);
        if (!ticket.isPresent()) return null;
        int[][] ticketNumbers = getTicketNumbers(ticket.get());
        TambolaTicket tambolaTicket = changeTicketToTambolaTicket(ticket.get());
        result.put("ticketNumbers", ticketNumbers);
        result.put("allTicketNumbers", tambolaTicket.numbers);
        return result;
    }

    private int[][] getTicketNumbers(Ticket ticket) {
        String[] rows = ticket.getNumbers().split(";");
        int[][] numbers = new int[3][5];
        int[][] allNumbers = new int[3][9];
        for (int i = 0; i < 3; i++) {
            String[] nums = rows[i].split(",");
            int columnCounter = 0;
            for (int j = 0; j < 9; j++) {
                if (!nums[j].equals("0")) {
                    numbers[i][columnCounter] = Integer.parseInt(nums[j]);
                    columnCounter++;
                }
            }
        }
        return numbers;
    }


    public TambolaTicket[] checkAndGetTickets(Player player) {
        List<Ticket> tickets = ticketRepository.findAllByPlayer(player);
        System.out.println(tickets);
        TambolaTicket[] tambolaTickets = changeTicketsToTambolaTickets(tickets);
        System.out.println(Arrays.toString(tambolaTickets));
        return tambolaTickets;
    }


    private TambolaTicket[] changeTicketsToTambolaTickets(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return null;
        }
        TambolaTicket[] tambolaTickets = new TambolaTicket[tickets.size()];
        for (int i = 0; i < tickets.size(); i++) {
            tambolaTickets[i] = changeTicketToTambolaTicket(tickets.get(i));
        }
        return tambolaTickets;
    }

    private TambolaTicket changeTicketToTambolaTicket(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        String[] rows = ticket.getNumbers().split(";");
        TambolaTicket tambolaTicket = new TambolaTicket();
        tambolaTicket.setId(ticket.getId());
        for (int i = 0; i < rows.length; i++) {
            String[] numsInString = rows[i].split(",");
            for (int j = 0; j < numsInString.length; j++) {
                tambolaTicket.numbers[i][j] = Integer.parseInt(numsInString[j]);
            }
        }
        return tambolaTicket;
    }

    public Ticket getTicket(Integer ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        return ticket.orElse(null);
    }


}
